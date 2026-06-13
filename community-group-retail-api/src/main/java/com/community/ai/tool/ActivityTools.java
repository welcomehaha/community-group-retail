package com.community.ai.tool;

import com.community.ai.mapper.AiKnowledgeDocumentMapper;
import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AiKnowledgeDocument;
import com.community.vo.ActivityKnowledgeVO;
import com.community.vo.OrderStatusVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动场景工具，基于现有知识库和订单金额拼装可解释的活动上下文。
 */
@Component
public class ActivityTools {

    private static final String ACTIVITY_DOC_TYPE = "activity";

    private final AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;
    private final OrderTools orderTools;
    private final CustomerAiToolLogService customerAiToolLogService;

    public ActivityTools(AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper,
                         OrderTools orderTools,
                         CustomerAiToolLogService customerAiToolLogService) {
        this.aiKnowledgeDocumentMapper = aiKnowledgeDocumentMapper;
        this.orderTools = orderTools;
        this.customerAiToolLogService = customerAiToolLogService;
    }

    /**
     * 查询活动/优惠券相关知识，并结合最近订单金额补充解释上下文。
     */
    @Tool(description = "查询优惠券、满减、红包、新人活动等规则说明，并可结合用户最近订单金额生成解释上下文")
    public ActivityKnowledgeVO queryActivityKnowledge(Long userId, Long conversationId, String keyword) {
        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentMapper.listEnabledByDocType(ACTIVITY_DOC_TYPE);
        if (documents == null || documents.isEmpty()) {
            saveLog(userId, conversationId, "queryActivityKnowledge", buildRequest(keyword), null, false, "活动知识不存在");
            return null;
        }

        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        AiKnowledgeDocument matchedDocument = null;
        for (AiKnowledgeDocument document : documents) {
            String title = document.getTitle() == null ? "" : document.getTitle().toLowerCase();
            String content = document.getContent() == null ? "" : document.getContent().toLowerCase();
            if (!StringUtils.hasText(normalizedKeyword) || title.contains(normalizedKeyword) || content.contains(normalizedKeyword)) {
                matchedDocument = document;
                break;
            }
        }
        if (matchedDocument == null) {
            matchedDocument = documents.get(0);
        }

        ActivityKnowledgeVO vo = new ActivityKnowledgeVO();
        vo.setDocumentId(matchedDocument.getId());
        vo.setTitle(matchedDocument.getTitle());
        vo.setDocType(matchedDocument.getDocType());
        vo.setContent(buildActivityContent(userId, matchedDocument));
        vo.setOrderRelated(Boolean.TRUE);

        saveLog(userId, conversationId, "queryActivityKnowledge", buildRequest(keyword), vo, true, null);
        return vo;
    }

    /**
     * 将活动知识与用户最近订单金额串联，形成轻量活动解释上下文。
     */
    private String buildActivityContent(Long userId, AiKnowledgeDocument matchedDocument) {
        StringBuilder contentBuilder = new StringBuilder(matchedDocument.getContent() == null ? "" : matchedDocument.getContent().trim());
        OrderStatusVO latestOrder = orderTools.queryLatestOrder(userId, null);
        if (latestOrder != null && latestOrder.getAmount() != null) {
            contentBuilder.append("\n当前用户最近订单金额为 ").append(latestOrder.getAmount()).append(" 元。");
            contentBuilder.append(" 如涉及满减、起用门槛、券后金额说明，请结合该金额判断是否满足条件，但不得编造未配置的活动规则。");
        }
        return contentBuilder.toString();
    }

    private Map<String, Object> buildRequest(String keyword) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("keyword", keyword);
        return request;
    }

    private void saveLog(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage) {
        customerAiToolLogService.saveToolCall(userId, conversationId, toolName, request, response, success, errorMessage);
    }
}
