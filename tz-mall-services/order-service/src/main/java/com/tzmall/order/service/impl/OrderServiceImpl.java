package com.tzmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.api.item.feign.ItemFeignClient;
import com.tzmall.api.order.dto.CreateOrderDTO;
import com.tzmall.api.order.dto.OrderDTO;
import com.tzmall.api.order.dto.OrderDetailDTO;
import com.tzmall.api.order.dto.OrderItemDTO;
import com.tzmall.api.order.dto.PayOrderDTO;
import com.tzmall.api.order.dto.PayOrderResultDTO;
import com.tzmall.api.user.dto.UserDTO;
import com.tzmall.api.user.feign.UserFeignClient;
import com.tzmall.common.core.result.Result;
import com.tzmall.common.mq.util.RabbitMqUtil;
import com.tzmall.common.redis.util.RedisUtil;
import com.tzmall.order.entity.Order;
import com.tzmall.order.entity.OrderDetail;
import com.tzmall.order.mapper.OrderDetailMapper;
import com.tzmall.order.mapper.OrderMapper;
import com.tzmall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import io.seata.spring.annotation.GlobalTransactional;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ItemFeignClient itemFeignClient;
    private final UserFeignClient userFeignClient;
    private final OrderDetailMapper orderDetailMapper;
    private final RabbitMqUtil rabbitMqUtil;
    private final RedisUtil redisUtil;

    @Override
    @GlobalTransactional(name = "create-order-tx", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Result<OrderDTO> createOrder(CreateOrderDTO createOrderDTO) {
        Long userId = createOrderDTO.getUserId();
        List<OrderItemDTO> orderItems = createOrderDTO.getItems();

        Result<UserDTO> userResult = userFeignClient.getUserById(userId);
        if (userResult == null || userResult.getData() == null) {
            log.warn("创建订单失败：用户不存在，用户ID={}", userId);
            return Result.fail(400, "用户不存在");
        }

        if (orderItems == null || orderItems.isEmpty()) {
            return Result.fail(400, "订单商品不能为空");
        }

        List<Long> itemIds = orderItems.stream()
                .map(OrderItemDTO::getItemId)
                .collect(Collectors.toList());

        Map<Long, ItemDTO> itemMap = new java.util.HashMap<>();

        for (Long itemId : itemIds) {
            Result<ItemDTO> itemResult = itemFeignClient.getItemById(itemId);
            if (itemResult == null || itemResult.getData() == null) {
                log.warn("创建订单失败：商品不存在，商品ID={}", itemId);
                return Result.fail(400, "商品不存在：" + itemId);
            }
            ItemDTO item = itemResult.getData();
            itemMap.put(itemId, item);

            if (item.getStatus() != 1) {
                log.warn("创建订单失败：商品已下架，商品ID={}", itemId);
                return Result.fail(400, "商品已下架：" + item.getName());
            }
        }

        for (OrderItemDTO orderItem : orderItems) {
            ItemDTO item = itemMap.get(orderItem.getItemId());
            if (item.getStock() < orderItem.getNum()) {
                log.warn("创建订单失败：库存不足，商品ID={}，库存={}，需求={}",
                        item.getId(), item.getStock(), orderItem.getNum());
                return Result.fail(400, "商品库存不足：" + item.getName());
            }
        }

        for (OrderItemDTO orderItem : orderItems) {
            Result<Void> deductResult = itemFeignClient.deductStock(orderItem.getItemId(), orderItem.getNum());
            if (deductResult == null || deductResult.getCode() != 200) {
                log.error("扣减库存失败，商品ID={}，数量={}", orderItem.getItemId(), orderItem.getNum());
                return Result.fail(500, "扣减库存失败：" + itemMap.get(orderItem.getItemId()).getName());
            }
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDTO orderItem : orderItems) {
            ItemDTO item = itemMap.get(orderItem.getItemId());
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(orderItem.getNum())));
        }

        Order order = new Order();
        order.setOrderNo(System.currentTimeMillis());
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(order);

        List<OrderDetailDTO> orderDetailDTOs = new ArrayList<>();
        for (OrderItemDTO orderItem : orderItems) {
            ItemDTO item = itemMap.get(orderItem.getItemId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setItemId(item.getId());
            orderDetail.setItemName(item.getName());
            orderDetail.setItemPrice(item.getPrice());
            orderDetail.setNum(orderItem.getNum());
            orderDetailMapper.insert(orderDetail);

            OrderDetailDTO detailDTO = new OrderDetailDTO();
            detailDTO.setId(orderDetail.getId());
            detailDTO.setOrderId(order.getId());
            detailDTO.setItemId(item.getId());
            detailDTO.setItemName(item.getName());
            detailDTO.setItemPrice(item.getPrice());
            detailDTO.setNum(orderItem.getNum());
            orderDetailDTOs.add(detailDTO);
        }

        try {
            rabbitMqUtil.sendOrderDelayMessage(String.valueOf(order.getOrderNo()));
            log.info("订单延迟消息发送成功，订单号={}", order.getOrderNo());
        } catch (Exception e) {
            log.error("发送订单延迟消息失败，订单号={}", order.getOrderNo(), e);
        }

        OrderDTO orderDTO = buildOrderDTO(order, orderDetailDTOs);

        // 缓存订单信息
        String cacheKeyId = "order:detail:id:" + order.getId();
        String cacheKeyNo = "order:detail:no:" + order.getOrderNo();
        redisUtil.set(cacheKeyId, orderDTO, 30, TimeUnit.MINUTES);
        redisUtil.set(cacheKeyNo, orderDTO, 30, TimeUnit.MINUTES);
        log.info("订单创建成功并缓存，订单ID={}，订单号={}", order.getId(), order.getOrderNo());

        // 清除用户订单列表缓存
        clearUserOrderListCache(userId);

        return Result.success(orderDTO);
    }

    @Override
    public Result<OrderDTO> getOrderById(Long id) {
        try {
            // 获取缓存
            String cacheKey = "order:detail:id:" + id;
            Object cache = redisUtil.get(cacheKey);
            if (cache != null && cache instanceof OrderDTO) {
                return Result.success((OrderDTO) cache);
            }
            // 查询数据库（如果缓存中没有）
            Order order = baseMapper.selectById(id);
            if (order == null) {
                // 缓存穿透防护：缓存空值，短时间过期
                redisUtil.set(cacheKey, "", 5, TimeUnit.MINUTES);
                log.warn("订单不存在，订单ID={}", id);
                return Result.fail(404, "订单不存在");
            }

            OrderDTO orderDTO = findOrderDetails(order);
            redisUtil.set(cacheKey, orderDTO, 30, TimeUnit.MINUTES);
            log.debug("查询订单并缓存，订单ID={}", id);

            return Result.success(orderDTO);
        } catch (Exception e) {
            log.error("查询订单详情失败，订单ID={}", id, e);
            return Result.fail(500, "查询订单失败：" + e.getMessage());
        }
    }

    @Override
    public Result<OrderDTO> getOrderByOrderNo(String orderNo) {
        try {
            // 获取缓存
            String cacheKey = "order:detail:no:" + orderNo;
            Object cache = redisUtil.get(cacheKey);
            if (cache != null && cache instanceof OrderDTO) {
                return Result.success((OrderDTO) cache);
            }

            Long orderNoLong;
            try {
                orderNoLong = Long.parseLong(orderNo);
            } catch (NumberFormatException e) {
                log.warn("订单号格式错误，订单号={}", orderNo);
                return Result.fail(400, "订单号格式错误");
            }

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderNo, orderNoLong);
            Order order = baseMapper.selectOne(wrapper);

            if (order == null) {
                // 缓存穿透防护：缓存空值，短时间过期
                redisUtil.set(cacheKey, "", 5, TimeUnit.MINUTES);
                log.warn("订单不存在，订单号={}", orderNo);
                return Result.fail(404, "订单不存在");
            }

            OrderDTO orderDTO = findOrderDetails(order);
            redisUtil.set(cacheKey, orderDTO, 30, TimeUnit.MINUTES);
            log.debug("查询订单并缓存，订单号={}", orderNo);

            return Result.success(orderDTO);
        } catch (Exception e) {
            log.error("查询订单详情失败，订单号={}", orderNo, e);
            return Result.fail(500, "查询订单失败：" + e.getMessage());
        }
    }

    @Override
    public Result<IPage<OrderDTO>> listUserOrders(Long userId, int page, int size) {
        // 获取缓存
        String cacheKey = "order:list:user:" + userId + ":" + page + ":" + size;
        Object cache = redisUtil.get(cacheKey);
        if (cache != null) {
            return Result.success((IPage<OrderDTO>) cache);
        }

        Result<UserDTO> userResult = userFeignClient.getUserById(userId);
        if (userResult == null || userResult.getData() == null) {
            log.warn("用户不存在，用户ID={}", userId);
            return Result.fail(400, "用户不存在");
        }

        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = baseMapper.selectPage(pageParam, wrapper);

        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            OrderDTO orderDTO = findOrderDetails(order);
            orderDTOs.add(orderDTO);
        }

        Page<OrderDTO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        resultPage.setRecords(orderDTOs);

        redisUtil.set(cacheKey, resultPage, 30, TimeUnit.MINUTES);
        log.debug("查询用户订单列表并缓存，用户ID={}", userId);

        return Result.success(resultPage);
    }

    @Override
    public Result<IPage<OrderDTO>> listAllOrders(int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = baseMapper.selectPage(pageParam, wrapper);

        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            OrderDTO orderDTO = findOrderDetails(order);
            orderDTOs.add(orderDTO);
        }

        Page<OrderDTO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        resultPage.setRecords(orderDTOs);

        return Result.success(resultPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateOrderStatus(Long orderId, Integer status) {
        Order order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(order);

        String cacheKeyId = "order:detail:id:" + orderId;
        String cacheKeyNo = "order:detail:no:" + order.getOrderNo();
        redisUtil.delete(cacheKeyId);
        redisUtil.delete(cacheKeyNo);
        clearUserOrderListCache(order.getUserId());

        log.info("订单状态更新成功，订单ID={}，新状态={}", orderId, status);
        return Result.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelOrder(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在，无法取消，订单ID={}", orderId);
            return Result.fail(404, "订单不存在");
        }

        if (order.getStatus() != 0) {
            log.warn("订单状态不是待支付，无法取消，订单ID={}，状态={}", orderId, order.getStatus());
            return Result.fail(400, "只有待支付的订单才能取消");
        }

        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(detailWrapper);

        for (OrderDetail detail : orderDetails) {
            try {
                itemFeignClient.addStock(detail.getItemId(), detail.getNum());
                log.info("订单取消成功，已返还库存，商品ID={}，数量={}", detail.getItemId(), detail.getNum());
            } catch (Exception e) {
                log.error("订单取消时返还库存失败，商品ID={}，数量={}", detail.getItemId(), detail.getNum(), e);
            }
        }

        order.setStatus(5);
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(order);

        // 清除订单缓存
        String cacheKeyId = "order:detail:id:" + orderId;
        String cacheKeyNo = "order:detail:no:" + order.getOrderNo();
        redisUtil.delete(cacheKeyId);
        redisUtil.delete(cacheKeyNo);
        log.debug("清除订单缓存，订单ID={}，订单号={}", orderId, order.getOrderNo());

        // 清除用户订单列表缓存
        clearUserOrderListCache(order.getUserId());

        log.info("订单取消成功，订单ID={}，订单号={}", orderId, order.getOrderNo());
        return Result.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PayOrderResultDTO> payOrder(PayOrderDTO payOrderDTO) {
        Long orderId = payOrderDTO.getOrderId();

        // 查询订单
        Order order = baseMapper.selectById(orderId);
        if (order == null) {
            log.warn("支付失败：订单不存在，订单ID={}", orderId);
            return Result.fail(404, "订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            log.warn("支付失败：订单状态不允许支付，订单ID={}，状态={}", orderId, order.getStatus());
            return Result.fail(400, "只有待支付的订单才能支付");
        }

        // 验证用户
        Result<UserDTO> userResult = userFeignClient.getUserById(order.getUserId());
        if (userResult == null || userResult.getData() == null) {
            log.warn("支付失败：用户不存在，用户ID={}", order.getUserId());
            return Result.fail(400, "用户不存在");
        }

        // 模拟支付过程 - 实际项目中这里会调用支付平台接口
        log.info("开始支付，订单ID={}，订单号={}，支付方式={}", 
                orderId, order.getOrderNo(), payOrderDTO.getPayMethod());

        // 假设支付成功 - 模拟调用第三方支付接口
        boolean paySuccess = true;

        if (paySuccess) {
            // 更新订单状态为已支付（待发货）
            order.setStatus(1);
            order.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(order);

            // 清除订单缓存
            String cacheKeyId = "order:detail:id:" + orderId;
            String cacheKeyNo = "order:detail:no:" + order.getOrderNo();
            redisUtil.delete(cacheKeyId);
            redisUtil.delete(cacheKeyNo);
            log.debug("清除订单缓存，订单ID={}，订单号={}", orderId, order.getOrderNo());

            // 清除用户订单列表缓存
            clearUserOrderListCache(order.getUserId());

            // 构建支付结果
            PayOrderResultDTO result = PayOrderResultDTO.builder()
                    .orderId(orderId)
                    .orderNo(String.valueOf(order.getOrderNo()))
                    .payStatus(1)
                    .payTime(LocalDateTime.now())
                    .payUrl(null) // 模拟支付，不返回支付URL
                    .build();

            log.info("订单支付成功，订单ID={}，订单号={}", orderId, order.getOrderNo());
            return Result.success(result);
        } else {
            log.warn("订单支付失败，订单ID={}，订单号={}", orderId, order.getOrderNo());
            return Result.fail(500, "支付失败");
        }
    }

    private OrderDTO findOrderDetails(Order order) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, order.getId());
        List<OrderDetail> details = orderDetailMapper.selectList(wrapper);

        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetail detail : details) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setId(detail.getId());
            dto.setOrderId(detail.getOrderId());
            dto.setItemId(detail.getItemId());
            dto.setItemName(detail.getItemName());
            dto.setItemPrice(detail.getItemPrice());
            dto.setNum(detail.getNum());
            detailDTOs.add(dto);
        }

        return buildOrderDTO(order, detailDTOs);
    }

    private OrderDTO buildOrderDTO(Order order, List<OrderDetailDTO> orderDetails) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(String.valueOf(order.getOrderNo()));
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setStatusDesc(getStatusDesc(order.getStatus()));
        dto.setOrderDetails(orderDetails);
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        return dto;
    }

    private void clearUserOrderListCache(Long userId) {
        String pattern = "order:list:user:" + userId + ":*";
        Long count = redisUtil.deleteByPattern(pattern);
        log.info("清除用户订单列表缓存，用户ID={}，删除缓存数量={}", userId, count);
    }

    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "待发货";
            case 2:
                return "待收货";
            case 3:
                return "待评价";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知";
        }
    }
}
