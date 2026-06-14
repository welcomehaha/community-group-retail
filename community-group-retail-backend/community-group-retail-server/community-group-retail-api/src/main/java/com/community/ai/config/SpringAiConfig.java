package com.community.ai.config;

import com.community.ai.tool.OrderTools;
import com.community.ai.tool.ProductTools;
import com.community.ai.tool.StoreTools;
import com.community.ai.tool.ActivityTools;
import com.community.ai.tool.UserProfileTools;
import com.community.ai.tool.DeliveryScopeTools;
import com.community.properties.AiProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiConfig {

    /**
     * 配置基于窗口的聊天记忆，避免上下文无限增长
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
    }

    /**
     * 将现有业务工具注册为 Spring AI Tool Calling 工具集
     */
    @Bean
    public ToolCallbackProvider customerAiToolCallbackProvider(OrderTools orderTools,
                                                               ProductTools productTools,
                                                               StoreTools storeTools,
                                                               ActivityTools activityTools,
                                                               UserProfileTools userProfileTools,
                                                               DeliveryScopeTools deliveryScopeTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(orderTools, productTools, storeTools, activityTools, userProfileTools, deliveryScopeTools)
                .build();
    }

    /**
     * 构建客服 AI 专用 ChatClient，统一挂默认系统提示词和工具集
     */
    @Bean
    @ConditionalOnProperty(prefix = "community.ai", name = "enabled", havingValue = "true")
    public ChatClient customerAiChatClient(ChatClient.Builder chatClientBuilder,
                                           ToolCallbackProvider customerAiToolCallbackProvider,
                                           AiProperties aiProperties,
                                           ObjectProvider<VectorStore> customerAiVectorStoreProvider) {
        ChatClient.Builder builder = chatClientBuilder
            .defaultSystem(aiProperties.getSystemPrompt())
            .defaultToolCallbacks(customerAiToolCallbackProvider);

        VectorStore customerAiVectorStore = customerAiVectorStoreProvider.getIfAvailable();
        if (customerAiVectorStore != null) {
            QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(customerAiVectorStore)
                .searchRequest(SearchRequest.builder()
                    .topK(aiProperties.getTopK())
                    .similarityThreshold(aiProperties.getSimilarityThreshold())
                    .build())
                .build();
            builder.defaultAdvisors(questionAnswerAdvisor);
        }

        return builder.build();
    }
}
