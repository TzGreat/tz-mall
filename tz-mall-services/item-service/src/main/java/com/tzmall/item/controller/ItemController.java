package com.tzmall.item.controller;

import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.api.item.dto.ItemRequestDTO;
import com.tzmall.common.core.annotation.RequireAdmin;
import com.tzmall.common.core.result.Result;
import com.tzmall.item.service.ItemSearchService;
import com.tzmall.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
@Tag(name = "商品管理", description = "商品CRUD操作")
public class ItemController {

    private final ItemService itemService;
    private final ItemSearchService itemSearchService;

    public ItemController(ItemService itemService, ItemSearchService itemSearchService) {
        this.itemService = itemService;
        this.itemSearchService = itemSearchService;
    }

    @GetMapping("/list")
    @Operation(summary = "获取商品列表", description = "获取所有上架的商品")
    public Result<List<ItemDTO>> getItemList() {
        return itemService.getItemList();
    }

    @PostMapping("/add")
    @RequireAdmin
    @Operation(summary = "添加商品", description = "添加商品到数据库")
    public Result<ItemDTO> addItem(
            @Parameter(description = "商品添加信息") @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        return itemService.addItem(itemRequestDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品详情", description = "根据商品ID查询商品详情")
    public Result<ItemDTO> getItemById(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PutMapping("/{id}")
    @RequireAdmin
    @Operation(summary = "更新商品", description = "根据商品ID更新商品信息")
    public Result<ItemDTO> updateItem(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "商品更新信息") @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        return itemService.updateItem(id, itemRequestDTO);
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    @Operation(summary = "删除商品", description = "根据商品ID删除商品")
    public Result<ItemDTO> deleteItem(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        return itemService.deleteItem(id);
    }

    @PostMapping("/{id}/stock/deduct")
    @Operation(summary = "扣减库存", description = "根据商品ID扣减库存")
    public Result<Void> deductStock(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "商品数量") @RequestParam Integer quantity) {
        return itemService.deductStock(id, quantity);
    }

    @PostMapping("/{id}/stock/add")
    @Operation(summary = "增加库存", description = "根据商品ID增加库存")
    public Result<Void> addStock(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "商品数量") @RequestParam Integer quantity) {
        return itemService.addStock(id, quantity);
    }

    @GetMapping("/search")
    @Operation(summary = "商品搜索", description = "根据关键词搜索商品")
    public Result<List<ItemDTO>> searchItems(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<ItemDTO> items = itemSearchService.searchItems(keyword);
        return Result.success(items);
    }
}
