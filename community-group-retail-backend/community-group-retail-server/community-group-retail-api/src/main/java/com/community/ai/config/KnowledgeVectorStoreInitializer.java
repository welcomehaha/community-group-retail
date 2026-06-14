package com.community.ai.config;

import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.properties.AiProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后初始化知识向量索引，保证 Spring AI RAG 检索链路可直接使用
 */
@Component
public class KnowledgeVectorStoreInitializer implements CommandLineRunner {

    private final CustomerAiKnowledgeService customerAiKnowledgeService;
    private final AiProperties aiProperties;

    public KnowledgeVectorStoreInitializer(CustomerAiKnowledgeService customerAiKnowledgeService,
                                           AiProperties aiProperties) {
        this.customerAiKnowledgeService = customerAiKnowledgeService;
        this.aiProperties = aiProperties;
    }

    @Override
    public void run(String... args) {
        // 未启用 Spring AI 时跳过向量索引初始化，避免因缺少外部模型配置影响主链路启动
        if (!Boolean.TRUE.equals(aiProperties.getEnabled())) {
            return;
        }
        customerAiKnowledgeService.rebuildKnowledgeIndex();
    }
}
