package com.tzmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.api.item.dto.ItemRequestDTO;
import com.tzmall.common.core.result.Result;
import com.tzmall.item.entity.Item;
import jakarta.validation.Valid;
import java.util.List;

public interface ItemService extends IService<Item> {
    Result<List<ItemDTO>> getItemList();
    
    Result<ItemDTO> getItemById(Long id);

    Result<ItemDTO> addItem(ItemRequestDTO itemRequestDTO);

    Result<ItemDTO> updateItem(Long id, ItemRequestDTO itemRequestDTO);

    Result<ItemDTO> deleteItem(Long id);

    Result<Void> deductStock(Long id, Integer quantity);

    Result<Void> addStock(Long id, Integer quantity);
}
