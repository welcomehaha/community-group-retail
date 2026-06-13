package com.community.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    /**
     * 第一阶段先使用 SimpleVectorStore，满足演示和本地验证需求
     */
    @Bean
    @ConditionalOnProperty(prefix = "community.ai", name = "enabled", havingValue = "true")
    @ConditionalOnBean(EmbeddingModel.class)
    public VectorStore customerAiVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
