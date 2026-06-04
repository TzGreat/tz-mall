package com.tzmall.api.item.feign;

import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.common.core.result.Result;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-item", path = "/api/item")
public interface ItemFeignClient {
    /**
     * 根据商品ID查询商品详情
     */
    @GetMapping("/{id}")
     Result<ItemDTO> getItemById(@Parameter(description = "商品ID") @PathVariable Long id);

    /**
     * 扣减库存
     */
    @PostMapping("/{id}/stock/deduct")
     Result<Void> deductStock(@PathVariable Long id, @RequestParam Integer quantity);

    /**
     * 增加库存（用于订单取消时返还库存）
     */
    @PostMapping("/{id}/stock/add")
    Result<Void> addStock(@PathVariable Long id, @RequestParam Integer quantity);
}
