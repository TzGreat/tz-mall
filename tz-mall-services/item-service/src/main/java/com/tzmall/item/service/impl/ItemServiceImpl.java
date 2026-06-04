package com.tzmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.api.item.dto.ItemRequestDTO;
import com.tzmall.common.core.result.Result;
import com.tzmall.common.redis.util.RedisUtil;
import com.tzmall.item.document.ItemDocument;
import com.tzmall.item.entity.Item;
import com.tzmall.item.mapper.ItemMapper;
import com.tzmall.item.service.ItemSearchService;
import com.tzmall.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    private final RedisUtil redisUtil;
    private final ItemSearchService itemSearchService;

    public ItemServiceImpl(RedisUtil redisUtil, ItemSearchService itemSearchService) {
        this.redisUtil = redisUtil;
        this.itemSearchService = itemSearchService;
    }

    @Override
    public Result<List<ItemDTO>> getItemList() {
        String cacheKey = "item:list";
        Object cache = redisUtil.get(cacheKey);
        if (cache != null) {
            return Result.success((List<ItemDTO>) cache);
        }
        
        List<Item> items = this.list();
        List<ItemDTO> itemDTOs = items.stream()
                .filter(item -> item.getStatus() == 1)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        redisUtil.set(cacheKey, itemDTOs, 30, TimeUnit.MINUTES);
        return Result.success(itemDTOs);
    }

    @Override
    public Result<ItemDTO> getItemById(Long id) {
        String cacheKey = "item:detail:" + id;
        Object cache = redisUtil.get(cacheKey);
        if (cache != null) {
            return Result.success((ItemDTO) cache);
        }
        Item item = this.getById(id);
        if (item == null) {
            redisUtil.set(cacheKey, "", 5, TimeUnit.MINUTES);
            return Result.fail(404, "商品不存在");
        }

        ItemDTO dto = convertToDTO(item);
        redisUtil.set(cacheKey, dto, 30, TimeUnit.MINUTES);

        return Result.success(dto);
    }

    @Override
    public Result<ItemDTO> addItem(ItemRequestDTO itemRequestDTO) {
        Item item = new Item();
        item.setName(itemRequestDTO.getName());
        item.setPrice(itemRequestDTO.getPrice());
        item.setStock(itemRequestDTO.getStock());
        item.setImage(itemRequestDTO.getImage());
        item.setStatus(1);

        item.setDescription(itemRequestDTO.getDescription());
        item.setCategory(itemRequestDTO.getCategory());

        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        boolean flag = this.save(item);
        if (!flag) {
            return Result.fail(500, "商品添加失败");
        }
        
        redisUtil.delete("item:list");
        itemSearchService.syncItemToES(item);
        return Result.success("商品添加成功", convertToDTO(item));
    }

    @Override
    public Result<ItemDTO> updateItem(Long id, ItemRequestDTO itemRequestDTO) {
        Item item = this.getById(id);

        if (item == null) {
            return Result.fail(404, "商品不存在");
        }

        if (itemRequestDTO.getName() != null) {
            item.setName(itemRequestDTO.getName());
        }
        if (itemRequestDTO.getPrice() != null) {
            item.setPrice(itemRequestDTO.getPrice());
        }
        if (itemRequestDTO.getStock() != null) {
            item.setStock(itemRequestDTO.getStock());
        }
        if (itemRequestDTO.getImage() != null) {
            item.setImage(itemRequestDTO.getImage());
        }
        if (itemRequestDTO.getDescription() != null) {
            item.setDescription(itemRequestDTO.getDescription());
        }
        if (itemRequestDTO.getCategory() != null) {
            item.setCategory(itemRequestDTO.getCategory());
        }
        if (itemRequestDTO.getStatus() != null) {
            item.setStatus(itemRequestDTO.getStatus());
        }
        item.setUpdateTime(LocalDateTime.now());

        boolean flag = this.updateById(item);
        if (!flag) {
            return Result.fail(500, "商品更新失败");
        }

        redisUtil.delete("item:list");
        redisUtil.delete("item:detail:" + id);
        itemSearchService.syncItemToES(item);

        return Result.success("商品更新成功", convertToDTO(item));
    }

    @Override
    public Result<ItemDTO> deleteItem(Long id) {
        Item item = this.getById(id);

        if (item == null) {
            return Result.fail(404, "商品不存在");
        }
        item.setStatus(0);
        boolean flag = this.updateById(item);
        if (!flag) {
            return Result.fail(500, "商品删除失败");
        }

        redisUtil.delete("item:list");
        redisUtil.delete("item:detail:" + id);
        itemSearchService.removeItemFromES(id);

        return Result.success("商品删除成功", convertToDTO(item));
    }

    @Override
    public Result<Void> deductStock(Long id, Integer quantity) {
        Item item = this.getById(id);
        if (item == null || item.getStatus() == 0) {
            return Result.fail(404, "商品不存在");
        }
        if (item.getStock() < quantity) {
            return Result.fail(400, "商品库存不足");
        }
        boolean flag = this.update()
                .eq("id", id)
                .ge("stock", quantity)
                .setSql("stock=stock-" + quantity)
                .update();
        if (!flag) {
            return Result.fail(400, "库存扣减失败");
        }
        
        Item updatedItem = this.getById(id);
        redisUtil.delete("item:list");
        redisUtil.delete("item:detail:" + id);
        itemSearchService.syncItemToES(updatedItem);
        
        return Result.success();
    }

    @Override
    public Result<Void> addStock(Long id, Integer quantity) {
        Item item = this.getById(id);
        if (item == null || item.getStatus() == 0) {
            return Result.fail(404, "商品不存在");
        }
        boolean flag = this.update()
                .eq("id", id)
                .setSql("stock=stock+" + quantity)
                .update();
        if (!flag) {
            return Result.fail(500, "库存增加失败");
        }
        
        Item updatedItem = this.getById(id);
        redisUtil.delete("item:list");
        redisUtil.delete("item:detail:" + id);
        itemSearchService.syncItemToES(updatedItem);
        
        return Result.success();
    }


    private ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();

        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setStock(item.getStock());
        itemDTO.setImage(item.getImage());
        itemDTO.setStatus(item.getStatus());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setCategory(item.getCategory());
        itemDTO.setCreateTime(item.getCreateTime());
        itemDTO.setUpdateTime(item.getUpdateTime());

        return itemDTO;
    }
}
