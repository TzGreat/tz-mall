package com.tzmall.item.config;

import com.tzmall.common.es.util.ElasticsearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private static final String INDEX_NAME = "item";

    private final ElasticsearchUtil elasticsearchUtil;

    @Override
    public void run(String... args) {
        try {
            // 检查索引是否存在，不存在则创建
            boolean indexExists = elasticsearchUtil.indexExists(INDEX_NAME);
            if (!indexExists) {
                boolean created = elasticsearchUtil.createIndex(INDEX_NAME);
                if (created) {
                    log.info("成功创建商品索引: {}", INDEX_NAME);
                } else {
                    log.warn("创建商品索引失败: {}", INDEX_NAME);
                }
            } else {
                log.info("商品索引已存在: {}", INDEX_NAME);
            }
        } catch (Exception e) {
            log.error("初始化商品索引时出错", e);
            log.warn("注意: 请确保 Elasticsearch 服务运行在 http://localhost:9200");
        }
    }
}
