package com.community.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "community.ai")
@Data
public class AiProperties {

    /**
     * 是否开启真实模型调用
     */
    private Boolean enabled = false;

    /**
     * 模型名称
     */
    private String model;

    /**
     * Embedding 模型名称
     */
    private String embeddingModel;

    /**
     * RAG 检索返回文档数量
     */
    private Integer topK = 4;

    /**
     * RAG 相似度阈值
     */
    private Double similarityThreshold = 0.6D;

    /**
     * 系统提示词
     */
    private String systemPrompt = "你是社区团购即时零售平台客服助手。涉及订单、库存、金额、配送状态时，必须基于提供的业务查询结果作答，不得编造。";

    /**
     * 超时时间说明字段，当前沿用 HttpClientUtil 默认超时
     */
    private Integer timeoutMillis = 5000;
}
