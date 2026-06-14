package com.community.ai.service;

import com.community.vo.AiConversationVO;
import com.community.vo.AiMessageVO;

import java.util.List;

public interface CustomerAiConversationService {

    Long createConversation(Long userId, String title);

    List<AiConversationVO> listConversations(Long userId);

    List<AiMessageVO> listMessages(Long userId, Long conversationId);

    void deleteConversation(Long userId, Long conversationId);
}
