package com.community.ai.service.impl;

import com.community.ai.dto.AiLlmReply;
import com.community.ai.service.CustomerAiLlmService;
import com.community.entity.AiMessage;
import com.community.properties.AiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@ConditionalOnBean(ChatClient.class)
public class CustomerAiSpringLlmServiceImpl implements CustomerAiLlmService {

    private final ChatClient customerAiChatClient;
    private final ChatMemory chatMemory;
    private final AiProperties aiProperties;

    public CustomerAiSpringLlmServiceImpl(ChatClient customerAiChatClient,
                                          ChatMemory chatMemory,
                                          AiProperties aiProperties) {
        this.customerAiChatClient = customerAiChatClient;
        this.chatMemory = chatMemory;
        this.aiProperties = aiProperties;
    }

    @Override
    public AiLlmReply chat(String userMessage, String context, String systemPrompt, List<AiMessage> history) {
        AiLlmReply reply = new AiLlmReply();
        if (!Boolean.TRUE.equals(aiProperties.getEnabled())) {
            reply.setSuccess(false);
            reply.setErrorMessage("Spring AI 未启用");
            return reply;
        }
        if (!StringUtils.hasText(userMessage)) {
            reply.setSuccess(false);
            reply.setErrorMessage("用户消息不能为空");
            return reply;
        }

        try {
            String conversationId = resolveConversationId(history);
            restoreHistory(conversationId, history, context);

            String answer = customerAiChatClient.prompt()
                    // 按当前场景传入动态系统提示词，避免所有路由复用一份模板。
                    .system(system -> system.text(StringUtils.hasText(systemPrompt) ? systemPrompt : aiProperties.getSystemPrompt()))
                    .user(userMessage)
                    .call()
                    .content();

            if (!StringUtils.hasText(answer)) {
                reply.setSuccess(false);
                reply.setErrorMessage("Spring AI 返回内容为空");
                return reply;
            }

            chatMemory.add(conversationId, new UserMessage(userMessage));
            chatMemory.add(conversationId, new AssistantMessage(answer));

            reply.setSuccess(true);
            reply.setContent(answer.trim());
            return reply;
        } catch (Exception e) {
            reply.setSuccess(false);
            reply.setErrorMessage("Spring AI 调用失败: " + e.getMessage());
            return reply;
        }
    }

    /**
     * 根据历史消息恢复当前会话的上下文窗口
     */
    private void restoreHistory(String conversationId, List<AiMessage> history, String context) {
        if (StringUtils.hasText(context)) {
            chatMemory.add(conversationId, new SystemMessage("业务上下文：" + context));
        }
        if (history == null || history.isEmpty()) {
            return;
        }
        int start = Math.max(0, history.size() - 6);
        for (int i = start; i < history.size(); i++) {
            AiMessage aiMessage = history.get(i);
            if (!StringUtils.hasText(aiMessage.getContent())) {
                continue;
            }
            Message message = "assistant".equals(aiMessage.getRole())
                    ? new AssistantMessage(aiMessage.getContent())
                    : new UserMessage(aiMessage.getContent());
            chatMemory.add(conversationId, message);
        }
    }

    /**
     * 以首条历史消息的会话ID作为记忆键，不足时退化为默认会话
     */
    private String resolveConversationId(List<AiMessage> history) {
        if (history != null && !history.isEmpty() && history.get(0).getConversationId() != null) {
            return String.valueOf(history.get(0).getConversationId());
        }
        return "default-customer-ai";
    }
}
