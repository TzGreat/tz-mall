package com.tzmall.item.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.tzmall.api.item.dto.ItemDTO;
import com.tzmall.common.es.util.ElasticsearchUtil;
import com.tzmall.item.document.ItemDocument;
import com.tzmall.item.entity.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSearchService {

    private static final String INDEX_NAME = "item";

    private final ElasticsearchUtil elasticsearchUtil;

    public ItemDocument convertToDocument(Item item) {
        ItemDocument document = new ItemDocument();
        BeanUtils.copyProperties(item, document);
        return document;
    }

    public ItemDTO convertToDTO(ItemDocument document) {
        ItemDTO dto = new ItemDTO();
        BeanUtils.copyProperties(document, dto);
        return dto;
    }

    public void syncItemToES(Item item) {
        try {
            ItemDocument document = convertToDocument(item);
            boolean success = elasticsearchUtil.indexDocument(INDEX_NAME, String.valueOf(item.getId()), document);
            if (success) {
                log.info("商品数据同步到 ES 成功，商品ID: {}", item.getId());
            } else {
                log.warn("商品数据同步到 ES 失败，商品ID: {}", item.getId());
            }
        } catch (Exception e) {
            log.error("商品数据同步到 ES 失败，商品ID: {}", item.getId(), e);
        }
    }

    public void removeItemFromES(Long itemId) {
        try {
            boolean success = elasticsearchUtil.deleteDocument(INDEX_NAME, String.valueOf(itemId));
            if (success) {
                log.info("商品数据从 ES 删除成功，商品ID: {}", itemId);
            } else {
                log.warn("商品数据从 ES 删除失败，商品ID: {}", itemId);
            }
        } catch (Exception e) {
            log.error("商品数据从 ES 删除失败，商品ID: {}", itemId, e);
        }
    }

    public List<ItemDTO> searchItems(String keyword) {
        List<ItemDTO> result = new ArrayList<>();
        try {
            Query query = MatchQuery.of(m -> m
                    .field("name")
                    .query(keyword)
            )._toQuery();

            List<ItemDocument> documents = elasticsearchUtil.search(INDEX_NAME, query, ItemDocument.class);

            for (ItemDocument doc : documents) {
                result.add(convertToDTO(doc));
            }
        } catch (IOException e) {
            log.error("搜索商品失败，关键词: {}", keyword, e);
        }
        return result;
    }

    public List<ItemDTO> searchItemsByKeyword(String keyword) {
        return searchItems(keyword);
    }
}
