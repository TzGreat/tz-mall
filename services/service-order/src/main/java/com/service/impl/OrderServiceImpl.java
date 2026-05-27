package com.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.Order.been.Order;
import com.example.Product.been.Product;
import com.feign.ProductFeignClient;
import com.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    ProductFeignClient productFeignClient;

    @SentinelResource(value = "createOrder")
    @Override
    public Order create(Long productId, Long userId) {
        Product p = productFeignClient.getProductById(productId);

        Order order = new Order();
        order.setId(1L);
        order.setUserId(userId);

        order.setTotalPrice(p.getPrice().multiply(new BigDecimal(p.getNum())).longValue());

        order.setName("zs");
        order.setProducts(null);
        order.setAddress("北京");
        order.setProducts(Arrays.asList(p));

        return order;
    }

    private Product getProductFromRemote(long productId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/api/product/product/" + productId;

        log.info("从远程服务获取商品:{}", url);
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    //使用负载均衡获取商品
    private Product getProductFromRemoteWithLoadBalance(long productId) {
        ServiceInstance choose = loadBalancerClient.choose("service-product");

        String url = "http://" + choose.getHost() + ":" + choose.getPort() + "/api/product/product/" + productId;
        log.info("从远程服务获取商品:{}", url);
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    //基于注解负载均衡
    private Product getProductFromRemoteWithLoadBalanceA(long productId) {
        // 使用 DiscoveryClient 直接获取实例，不依赖 LoadBalancer
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        if (instances.isEmpty()) {
            throw new RuntimeException("未找到 service-product 服务实例");
        }
        // 简单的轮询策略
        ServiceInstance instance = instances.get(0);
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/api/product/product/" + productId;
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    //使用 DiscoveryClient 直接获取实例（不依赖 LoadBalancer）
    private Product getProductFromDiscoveryClient(long productId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        if (instances.isEmpty()) {
            throw new RuntimeException("未找到 service-product 服务实例");
        }
        //简单的轮询策略（取第一个实例）
        ServiceInstance instance = instances.get(0);
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/api/product/product/" + productId;
        log.info("从服务实例获取商品: {}", url);
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
}
