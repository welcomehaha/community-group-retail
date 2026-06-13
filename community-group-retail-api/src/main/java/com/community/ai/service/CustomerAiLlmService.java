package com.community.ai.service;

import com.community.ai.dto.AiLlmReply;
import com.community.entity.AiMessage;

import java.util.List;

public interface CustomerAiLlmService {

    /**
     * 统一封装大模型调用入口，支持按场景传入动态系统提示词。
     */
    AiLlmReply chat(String userMessage, String context, String systemPrompt, List<AiMessage> history);
}
