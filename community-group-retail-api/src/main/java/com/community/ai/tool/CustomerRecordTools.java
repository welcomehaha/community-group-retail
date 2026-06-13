package com.community.ai.tool;

import com.community.ai.mapper.AiConversationMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomerRecordTools {

    private final AiConversationMapper aiConversationMapper;

    public CustomerRecordTools(AiConversationMapper aiConversationMapper) {
        this.aiConversationMapper = aiConversationMapper;
    }

    public void updateConversationTitle(Long conversationId, String message) {
        if (conversationId == null || !StringUtils.hasText(message)) {
            return;
        }
        String title = message.length() > 12 ? message.substring(0, 12) : message;
        aiConversationMapper.updateTitle(conversationId, title);
    }
}
