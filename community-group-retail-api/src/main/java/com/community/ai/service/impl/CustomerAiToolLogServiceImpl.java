package com.community.ai.service.impl;

import com.alibaba.fastjson.JSON;
import com.community.ai.mapper.AiToolCallLogMapper;
import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AiToolCallLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerAiToolLogServiceImpl implements CustomerAiToolLogService {

    private final AiToolCallLogMapper aiToolCallLogMapper;

    public CustomerAiToolLogServiceImpl(AiToolCallLogMapper aiToolCallLogMapper) {
        this.aiToolCallLogMapper = aiToolCallLogMapper;
    }

    @Override
    public void saveToolCall(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage) {
        AiToolCallLog log = new AiToolCallLog();
        log.setUserId(userId);
        log.setConversationId(conversationId);
        log.setToolName(toolName);
        log.setRequestJson(request == null ? null : JSON.toJSONString(request));
        log.setResponseJson(response == null ? null : JSON.toJSONString(response));
        log.setSuccessFlag(success ? 1 : 0);
        log.setErrorMessage(errorMessage);
        log.setCreateTime(LocalDateTime.now());
        aiToolCallLogMapper.insert(log);
    }
}
