package com.community.ai.service.impl;

import com.community.ai.dto.AiLlmReply;
import com.community.ai.service.CustomerAiLlmService;
import com.community.entity.AiMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnMissingBean(CustomerAiLlmService.class)
public class CustomerAiDisabledLlmServiceImpl implements CustomerAiLlmService {

    @Override
    public AiLlmReply chat(String userMessage, String context, String systemPrompt, List<AiMessage> history) {
        AiLlmReply reply = new AiLlmReply();
        reply.setSuccess(false);
        reply.setErrorMessage("Spring AI 未启用或模型配置缺失");
        return reply;
    }
}
