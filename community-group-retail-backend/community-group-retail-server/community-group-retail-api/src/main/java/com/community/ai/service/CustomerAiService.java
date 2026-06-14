package com.community.ai.service;

import com.community.vo.AiChatVO;

public interface CustomerAiService {

    AiChatVO chat(Long userId, Long conversationId, String message);
}
