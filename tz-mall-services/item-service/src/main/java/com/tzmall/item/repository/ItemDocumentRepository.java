package com.tzmall.item.repository;

import com.tzmall.item.document.ItemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemDocumentRepository extends ElasticsearchRepository<ItemDocument, Long> {
}
