package com.tzmall.common.es.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch 工具类
 * <p>
 * 封装常用的 ES 操作，简化业务代码调用。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ElasticsearchUtil {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 创建索引
     *
     * @param indexName 索引名称
     * @return 是否成功
     */
    public boolean createIndex(String indexName) throws IOException {
        return elasticsearchClient.indices()
                .create(c -> c.index(indexName))
                .acknowledged();
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return 是否成功
     */
    public boolean deleteIndex(String indexName) throws IOException {
        return elasticsearchClient.indices()
                .delete(d -> d.index(indexName))
                .acknowledged();
    }

    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名称
     * @return 是否存在
     */
    public boolean indexExists(String indexName) throws IOException {
        return elasticsearchClient.indices()
                .exists(e -> e.index(indexName))
                .value();
    }

    /**
     * 索引文档
     *
     * @param indexName 索引名称
     * @param id        文档 ID
     * @param document  文档对象
     * @return 是否成功
     */
    public <T> boolean indexDocument(String indexName, String id, T document) throws IOException {
        Result result = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(id)
                .document(document)
        ).result();
        return result == Result.Created || result == Result.Updated;
    }

    /**
     * 批量索引文档
     *
     * @param indexName 索引名称
     * @param documents 文档列表（包含 id 和文档对象）
     * @return 是否成功
     */
    public <T> boolean bulkIndex(String indexName, List<DocumentWrapper<T>> documents) throws IOException {
        return elasticsearchClient.bulk(b -> {
            for (DocumentWrapper<T> wrapper : documents) {
                b.operations(op -> op.index(idx -> idx
                        .index(indexName)
                        .id(wrapper.id())
                        .document(wrapper.document())
                ));
            }
            return b;
        }).errors() == false;
    }

    /**
     * 获取文档
     *
     * @param indexName 索引名称
     * @param id        文档 ID
     * @param clazz     文档类型
     * @return 文档对象
     */
    public <T> T getDocument(String indexName, String id, Class<T> clazz) throws IOException {
        return elasticsearchClient.get(g -> g
                .index(indexName)
                .id(id)
                , clazz).source();
    }

    /**
     * 删除文档
     *
     * @param indexName 索引名称
     * @param id        文档 ID
     * @return 是否成功
     */
    public boolean deleteDocument(String indexName, String id) throws IOException {
        Result result = elasticsearchClient.delete(d -> d
                .index(indexName)
                .id(id)
        ).result();
        return result == Result.Deleted;
    }

    /**
     * 搜索文档
     *
     * @param indexName 索引名称
     * @param query     查询条件
     * @param clazz     文档类型
     * @return 文档列表
     */
    public <T> List<T> search(String indexName, Query query, Class<T> clazz) throws IOException {
        SearchResponse<T> response = elasticsearchClient.search(s -> s
                .index(indexName)
                .query(query)
                , clazz);

        List<T> results = new ArrayList<>();
        for (Hit<T> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    /**
     * 搜索文档（带分页）
     *
     * @param indexName 索引名称
     * @param query     查询条件
     * @param from      起始位置
     * @param size      每页大小
     * @param clazz     文档类型
     * @return 文档列表
     */
    public <T> List<T> search(String indexName, Query query, int from, int size, Class<T> clazz) throws IOException {
        SearchResponse<T> response = elasticsearchClient.search(s -> s
                .index(indexName)
                .query(query)
                .from(from)
                .size(size)
                , clazz);

        List<T> results = new ArrayList<>();
        for (Hit<T> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    /**
     * 文档包装类（用于批量操作）
     */
    public record DocumentWrapper<T>(String id, T document) {}
}
