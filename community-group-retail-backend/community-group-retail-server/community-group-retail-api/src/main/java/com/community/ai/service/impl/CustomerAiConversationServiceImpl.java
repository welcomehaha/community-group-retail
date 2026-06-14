package com.community.ai.service.impl;

import com.community.ai.mapper.AiConversationMapper;
import com.community.ai.mapper.AiMessageMapper;
import com.community.ai.service.CustomerAiConversationService;
import com.community.entity.AiConversation;
import com.community.entity.AiMessage;
import com.community.vo.AiConversationVO;
import com.community.vo.AiMessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerAiConversationServiceImpl implements CustomerAiConversationService {

    private final AiConversationMapper aiConversationMapper;
    private final AiMessageMapper aiMessageMapper;

    public CustomerAiConversationServiceImpl(AiConversationMapper aiConversationMapper, AiMessageMapper aiMessageMapper) {
        this.aiConversationMapper = aiConversationMapper;
        this.aiMessageMapper = aiMessageMapper;
    }

    @Override
    public Long createConversation(Long userId, String title) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setConversationType("customer_service");
        conversation.setTitle(StringUtils.hasText(title) ? title : "新客服会话");
        conversation.setStatus(1);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        aiConversationMapper.insert(conversation);
        return conversation.getId();
    }

    @Override
    public List<AiConversationVO> listConversations(Long userId) {
        List<AiConversation> conversations = aiConversationMapper.listByUserId(userId);
        List<AiConversationVO> result = new ArrayList<>();
        for (AiConversation conversation : conversations) {
            AiConversationVO vo = new AiConversationVO();
            vo.setConversationId(conversation.getId());
            vo.setTitle(conversation.getTitle());
            vo.setStatus(conversation.getStatus());
            vo.setCreateTime(conversation.getCreateTime());
            vo.setUpdateTime(conversation.getUpdateTime());
            AiMessage lastMessage = aiMessageMapper.getLastMessage(conversation.getId());
            if (lastMessage != null) {
                vo.setLastMessage(lastMessage.getContent());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<AiMessageVO> listMessages(Long userId, Long conversationId) {
        AiConversation conversation = aiConversationMapper.getByIdAndUserId(conversationId, userId);
        if (conversation == null) {
            return new ArrayList<>();
        }
        List<AiMessage> messages = aiMessageMapper.listByConversationId(conversationId);
        List<AiMessageVO> result = new ArrayList<>();
        for (AiMessage message : messages) {
            AiMessageVO vo = new AiMessageVO();
            BeanUtils.copyProperties(message, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void deleteConversation(Long userId, Long conversationId) {
        aiMessageMapper.deleteByConversationId(conversationId);
        aiConversationMapper.deleteById(conversationId, userId);
    }
}
