package com.community.ai.service;

public interface CustomerAiToolLogService {

    void saveToolCall(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage);
}
