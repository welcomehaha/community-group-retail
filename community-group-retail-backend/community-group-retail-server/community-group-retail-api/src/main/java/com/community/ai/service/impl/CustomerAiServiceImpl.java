package com.community.ai.service.impl;

import com.community.ai.enums.AiMessageRole;
import com.community.ai.enums.AiRouteType;
import com.community.ai.mapper.AiConversationMapper;
import com.community.ai.mapper.AiMessageMapper;
import com.community.ai.dto.AiLlmReply;
import com.community.ai.router.AiIntentAnalysis;
import com.community.ai.router.CustomerAiIntentClassifier;
import com.community.ai.router.CustomerAiScenePromptBuilder;
import com.community.ai.router.CustomerAiUserStateAssembler;
import com.community.ai.rag.KnowledgeMatchResult;
import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.ai.service.CustomerAiLlmService;
import com.community.ai.service.CustomerAiService;
import com.community.ai.tool.CustomerRecordTools;
import com.community.ai.tool.OrderTools;
import com.community.ai.tool.ProductTools;
import com.community.ai.tool.StoreTools;
import com.community.ai.tool.ActivityTools;
import com.community.ai.tool.DeliveryScopeTools;
import com.community.entity.AiConversation;
import com.community.entity.AiMessage;
import com.community.vo.AiChatVO;
import com.community.vo.ActivityKnowledgeVO;
import com.community.vo.DeliveryScopeCheckVO;
import com.community.vo.OrderStatusVO;
import com.community.vo.ProductInfoVO;
import com.community.vo.StoreStatusVO;
import com.community.properties.AiProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerAiServiceImpl implements CustomerAiService {

    private static final String ACTION_CONFIRM_TOKEN = "确认";
    private static final String ACTION_TYPE_REMIND_ORDER = "remind_order";
    private static final String ACTION_TYPE_CANCEL_ORDER = "cancel_order";
    private static final String ACTION_TYPE_AFTER_SALE = "after_sale";

    private final AiConversationMapper aiConversationMapper;
    private final AiMessageMapper aiMessageMapper;
    private final CustomerAiKnowledgeService customerAiKnowledgeService;
    private final CustomerAiLlmService customerAiLlmService;
    private final OrderTools orderTools;
    private final ProductTools productTools;
    private final StoreTools storeTools;
    private final ActivityTools activityTools;
    private final DeliveryScopeTools deliveryScopeTools;
    private final CustomerRecordTools customerRecordTools;
    private final CustomerAiIntentClassifier customerAiIntentClassifier;
    private final CustomerAiScenePromptBuilder customerAiScenePromptBuilder;
    private final CustomerAiUserStateAssembler customerAiUserStateAssembler;
    private final AiProperties aiProperties;

    public CustomerAiServiceImpl(AiConversationMapper aiConversationMapper,
                                 AiMessageMapper aiMessageMapper,
                                 CustomerAiKnowledgeService customerAiKnowledgeService,
                                 CustomerAiLlmService customerAiLlmService,
                                 OrderTools orderTools,
                                 ProductTools productTools,
                                 StoreTools storeTools,
                                 ActivityTools activityTools,
                                 DeliveryScopeTools deliveryScopeTools,
                                 CustomerRecordTools customerRecordTools,
                                 CustomerAiIntentClassifier customerAiIntentClassifier,
                                 CustomerAiScenePromptBuilder customerAiScenePromptBuilder,
                                 CustomerAiUserStateAssembler customerAiUserStateAssembler,
                                 AiProperties aiProperties) {
        this.aiConversationMapper = aiConversationMapper;
        this.aiMessageMapper = aiMessageMapper;
        this.customerAiKnowledgeService = customerAiKnowledgeService;
        this.customerAiLlmService = customerAiLlmService;
        this.orderTools = orderTools;
        this.productTools = productTools;
        this.storeTools = storeTools;
        this.activityTools = activityTools;
        this.deliveryScopeTools = deliveryScopeTools;
        this.customerRecordTools = customerRecordTools;
        this.customerAiIntentClassifier = customerAiIntentClassifier;
        this.customerAiScenePromptBuilder = customerAiScenePromptBuilder;
        this.customerAiUserStateAssembler = customerAiUserStateAssembler;
        this.aiProperties = aiProperties;
    }

    @Override
    public AiChatVO chat(Long userId, Long conversationId, String message) {
        AiConversation conversation = aiConversationMapper.getByIdAndUserId(conversationId, userId);
        if (conversation == null) {
            throw new IllegalArgumentException("会话不存在");
        }

        saveMessage(conversationId, userId, AiMessageRole.USER, message, "text", null, null, null);
        customerRecordTools.updateConversationTitle(conversationId, message);
        aiConversationMapper.touch(conversationId);

        AiChatVO response = route(userId, conversationId, message);
        saveMessage(conversationId, userId, AiMessageRole.ASSISTANT, response.getAnswer(), "text", response.getRoute(), response.getActionType(), null);
        aiConversationMapper.touch(conversationId);
        return response;
    }

    private AiChatVO route(Long userId, Long conversationId, String message) {
        AiIntentAnalysis intentAnalysis = customerAiIntentClassifier.classify(message);
        if (intentAnalysis == null || !StringUtils.hasText(intentAnalysis.getRoute())) {
            return buildFallbackReply(conversationId);
        }

        if (AiRouteType.ACTION.equals(intentAnalysis.getRoute())) {
            if (ACTION_TYPE_AFTER_SALE.equals(intentAnalysis.getActionType())) {
                return buildAfterSaleReply(userId, conversationId, message, intentAnalysis);
            }
            if (ACTION_TYPE_CANCEL_ORDER.equals(intentAnalysis.getActionType())) {
                return buildCancelOrderReply(userId, conversationId, message, intentAnalysis);
            }
            if (ACTION_TYPE_REMIND_ORDER.equals(intentAnalysis.getActionType())) {
                return buildReminderReply(userId, conversationId, message, intentAnalysis);
            }
        }
        if (AiRouteType.ORDER.equals(intentAnalysis.getRoute())) {
            return buildOrderReply(userId, conversationId, message, intentAnalysis);
        }
        if (AiRouteType.STORE.equals(intentAnalysis.getRoute())) {
            return buildStoreReply(userId, conversationId, intentAnalysis);
        }
        if (AiRouteType.PRODUCT.equals(intentAnalysis.getRoute())) {
            return buildProductReply(userId, conversationId, message, intentAnalysis);
        }
        if (AiRouteType.ACTIVITY.equals(intentAnalysis.getRoute())) {
            return buildActivityReply(userId, conversationId, message, intentAnalysis);
        }

        KnowledgeMatchResult match = customerAiKnowledgeService.match(message);
        if (match != null) {
            AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, message,
                    "知识库命中：" + match.getTitle() + "\n" + match.getContent(),
                    AiRouteType.FAQ, intentAnalysis);
            if (llmAnswer != null) {
                List<String> references = new ArrayList<>();
                references.add(match.getTitle());
                llmAnswer.setReferences(references);
                return llmAnswer;
            }
            AiChatVO vo = new AiChatVO();
            vo.setConversationId(conversationId);
            vo.setRoute(AiRouteType.FAQ);
            vo.setAnswer(match.getContent());
            List<String> references = new ArrayList<>();
            references.add(match.getTitle());
            vo.setReferences(references);
            return vo;
        }

        return buildFallbackReply(conversationId);
    }

    private AiChatVO buildOrderReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        if (containsAny(message, "催单", "提醒商家", "加急")) {
            return buildReminderReply(userId, conversationId, message, intentAnalysis);
        }
        String orderToken = intentAnalysis.getOrderToken();
        OrderStatusVO order = StringUtils.hasText(orderToken)
                ? orderTools.queryOrderStatus(userId, conversationId, orderToken)
                : orderTools.queryLatestOrder(userId, conversationId);

        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.ORDER);
        if (order == null) {
            vo.setAnswer("我暂时没有查到对应订单。你可以提供订单号，或者到订单列表确认最近订单是否已创建成功。");
            return vo;
        }
        String context = "订单查询结果：订单号=" + order.getOrderNumber()
                + "，订单状态=" + order.getStatusLabel()
                + "，支付状态=" + order.getPayStatusLabel()
                + "，下单时间=" + order.getOrderTime()
                + "，预计送达时间=" + order.getEstimatedDeliveryTime()
                + "，送达时间=" + order.getDeliveryTime();
        AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, message,
                buildChainedContext(userId, conversationId, intentAnalysis, context),
                AiRouteType.ORDER, intentAnalysis);
        if (llmAnswer != null) {
            return llmAnswer;
        }
        StringBuilder answer = new StringBuilder();
        answer.append("我帮你查到订单");
        answer.append(order.getOrderNumber() == null ? "" : " ").append(order.getOrderNumber());
        answer.append(" 当前状态为").append(order.getStatusLabel());
        answer.append("，支付状态为").append(order.getPayStatusLabel());
        if (order.getEstimatedDeliveryTime() != null) {
            answer.append("，预计送达时间为 ").append(order.getEstimatedDeliveryTime());
        } else if (order.getDeliveryTime() != null) {
            answer.append("，送达时间为 ").append(order.getDeliveryTime());
        }
        answer.append("。");
        vo.setAnswer(answer.toString());
        return vo;
    }

    /**
     * 活动场景优先走知识库，未命中时再回退到通用话术。
     */
    private AiChatVO buildActivityReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        ActivityKnowledgeVO activityKnowledge = activityTools.queryActivityKnowledge(userId, conversationId, message);
        if (activityKnowledge != null && StringUtils.hasText(activityKnowledge.getContent())) {
            AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, message,
                    "活动工具查询结果：标题=" + activityKnowledge.getTitle() + "，内容=" + activityKnowledge.getContent(),
                    AiRouteType.ACTIVITY, intentAnalysis);
            if (llmAnswer != null) {
                List<String> references = new ArrayList<>();
                references.add(activityKnowledge.getTitle());
                llmAnswer.setReferences(references);
                return llmAnswer;
            }
            AiChatVO vo = new AiChatVO();
            vo.setConversationId(conversationId);
            vo.setRoute(AiRouteType.ACTIVITY);
            vo.setAnswer(activityKnowledge.getContent());
            List<String> references = new ArrayList<>();
            references.add(activityKnowledge.getTitle());
            vo.setReferences(references);
            return vo;
        }

        KnowledgeMatchResult match = customerAiKnowledgeService.match(message);
        if (match != null) {
            AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, message,
                    "活动规则知识：标题=" + match.getTitle() + "，内容=" + match.getContent(),
                    AiRouteType.ACTIVITY, intentAnalysis);
            if (llmAnswer != null) {
                List<String> references = new ArrayList<>();
                references.add(match.getTitle());
                llmAnswer.setReferences(references);
                return llmAnswer;
            }
            AiChatVO vo = new AiChatVO();
            vo.setConversationId(conversationId);
            vo.setRoute(AiRouteType.ACTIVITY);
            vo.setAnswer(match.getContent());
            List<String> references = new ArrayList<>();
            references.add(match.getTitle());
            vo.setReferences(references);
            return vo;
        }
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.ACTIVITY);
        vo.setAnswer("我暂时没有命中对应的活动规则。你可以补充优惠券名称、活动标题或具体门槛，我再继续帮你查询；必要时建议转人工客服。");
        return vo;
    }

    /**
     * 催单属于真实业务动作，必须要求用户在消息中显式确认。
     */
    private AiChatVO buildReminderReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        String orderToken = intentAnalysis.getOrderToken();
        if (!StringUtils.hasText(orderToken)) {
            return buildActionReply(conversationId, ACTION_TYPE_REMIND_ORDER, "请先提供需要催单的订单号，并在消息中带上“确认”两个字，例如：确认催单 123456。");
        }
        if (!message.contains(ACTION_CONFIRM_TOKEN)) {
            return buildActionReply(conversationId, ACTION_TYPE_REMIND_ORDER, "催单属于真实业务动作。若你确认执行，请回复：确认催单 " + orderToken + "。");
        }
        try {
            String result = orderTools.remindOrder(userId, conversationId, orderToken);
            return buildActionReply(conversationId, ACTION_TYPE_REMIND_ORDER, result);
        } catch (IllegalArgumentException ex) {
            return buildActionReply(conversationId, ACTION_TYPE_REMIND_ORDER, ex.getMessage());
        }
    }

    /**
     * 取消订单仅放开待付款场景，避免 AI 误处理已支付订单退款。
     */
    private AiChatVO buildCancelOrderReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        String orderToken = intentAnalysis.getOrderToken();
        if (!StringUtils.hasText(orderToken)) {
            return buildActionReply(conversationId, ACTION_TYPE_CANCEL_ORDER, "请先提供需要取消的订单号，并在消息中带上“确认”两个字，例如：确认取消订单 123456。");
        }
        if (!message.contains(ACTION_CONFIRM_TOKEN)) {
            return buildActionReply(conversationId, ACTION_TYPE_CANCEL_ORDER, "取消订单属于高风险操作。若订单仍为待付款状态，请回复：确认取消订单 " + orderToken + "。");
        }
        try {
            String result = orderTools.cancelUnpaidOrder(userId, conversationId, orderToken);
            return buildActionReply(conversationId, ACTION_TYPE_CANCEL_ORDER, result);
        } catch (Exception ex) {
            return buildActionReply(conversationId, ACTION_TYPE_CANCEL_ORDER, ex.getMessage());
        }
    }

    /**
     * 售后申请先做结构化入库，后续可接人工工作台或自动审核流程。
     */
    private AiChatVO buildAfterSaleReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        String orderToken = intentAnalysis.getOrderToken();
        if (!StringUtils.hasText(orderToken)) {
            return buildActionReply(conversationId, ACTION_TYPE_AFTER_SALE, "请先提供订单号，并说明售后原因，例如：确认售后 123456 商品破损。");
        }
        if (!message.contains(ACTION_CONFIRM_TOKEN)) {
            return buildActionReply(conversationId, ACTION_TYPE_AFTER_SALE, "售后申请需要你的明确授权。若确认提交，请回复：确认售后 " + orderToken + " 原因说明。");
        }
        String reason = extractAfterSaleReason(message);
        if (!StringUtils.hasText(reason)) {
            return buildActionReply(conversationId, ACTION_TYPE_AFTER_SALE, "请补充售后原因，例如：确认售后 " + orderToken + " 商品破损，汤汁洒漏。");
        }
        try {
            String result = orderTools.createAfterSaleRequest(userId, conversationId, orderToken, reason, message);
            return buildActionReply(conversationId, ACTION_TYPE_AFTER_SALE, result);
        } catch (IllegalArgumentException ex) {
            return buildActionReply(conversationId, ACTION_TYPE_AFTER_SALE, ex.getMessage());
        }
    }

    private AiChatVO buildStoreReply(Long userId, Long conversationId, AiIntentAnalysis intentAnalysis) {
        StoreStatusVO store = storeTools.queryStoreStatus(userId, conversationId);
        DeliveryScopeCheckVO deliveryScope = deliveryScopeTools.checkDeliveryScope(userId, conversationId);
        AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, "门店营业状态查询",
                buildChainedContext(userId, conversationId, intentAnalysis,
                        "门店状态=" + store.getStatusLabel() + "，说明=" + store.getMessage()
                                + buildDeliveryScopeContext(deliveryScope)),
                AiRouteType.STORE, intentAnalysis);
        if (llmAnswer != null) {
            return llmAnswer;
        }
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.STORE);
        vo.setAnswer(buildStoreAnswer(store, deliveryScope));
        return vo;
    }

    private AiChatVO buildProductReply(Long userId, Long conversationId, String message, AiIntentAnalysis intentAnalysis) {
        String keyword = intentAnalysis.getProductKeyword();
        ProductInfoVO product = StringUtils.hasText(keyword) ? productTools.queryProductInfo(userId, conversationId, keyword) : null;
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.PRODUCT);
        if (product == null) {
            vo.setAnswer("我暂时没有定位到你要查询的商品。你可以直接告诉我商品名，我再帮你查是否起售。");
            return vo;
        }
        AiChatVO llmAnswer = tryLlmAnswer(userId, conversationId, message,
                "商品查询结果：商品名=" + product.getProductName()
                        + "，状态=" + product.getStatusLabel()
                        + "，价格=" + product.getPrice()
                        + "，说明=" + product.getDescription()
                        + "，是否可下单=" + product.getAvailable(),
                AiRouteType.PRODUCT, intentAnalysis);
        if (llmAnswer != null) {
            return llmAnswer;
        }
        vo.setAnswer("我帮你查到商品“" + product.getProductName() + "”当前状态为" + product.getStatusLabel()
                + "，价格为" + product.getPrice() + "元。"
                + (StringUtils.hasText(product.getDescription()) ? "商品说明：" + product.getDescription() + "。" : "")
                + (Boolean.TRUE.equals(product.getAvailable()) ? "当前可以下单。" : "当前暂不可购买。"));
        return vo;
    }

    /**
     * 优先尝试使用真实模型生成回复，失败后回退到规则回复
     */
    private AiChatVO tryLlmAnswer(Long userId, Long conversationId, String userMessage, String context, String route, AiIntentAnalysis intentAnalysis) {
        List<AiMessage> history = aiMessageMapper.listByConversationId(conversationId);
        String finalContext = enrichLlmContext(userId, userMessage, buildChainedContext(userId, conversationId, intentAnalysis, context));
        String systemPrompt = customerAiScenePromptBuilder.buildSystemPrompt(aiProperties.getSystemPrompt(), route, finalContext);
        AiLlmReply reply = customerAiLlmService.chat(userMessage, finalContext, systemPrompt, history);
        if (reply == null || !Boolean.TRUE.equals(reply.getSuccess()) || !StringUtils.hasText(reply.getContent())) {
            return null;
        }
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(route);
        vo.setAnswer(reply.getContent());
        return vo;
    }

    /**
     * 将用户状态感知上下文和当前任务上下文串联起来，为后续链式执行提供统一输入。
     */
    private String buildChainedContext(Long userId, Long conversationId, AiIntentAnalysis intentAnalysis, String baseContext) {
        String stateContext = customerAiUserStateAssembler.buildStateContext(userId, conversationId, intentAnalysis);
        if (!StringUtils.hasText(stateContext)) {
            return baseContext;
        }
        if (!StringUtils.hasText(baseContext)) {
            return stateContext;
        }
        return stateContext + "\n\n" + baseContext;
    }

    /**
     * 把订单场景的结构化摘要注入上下文，要求模型必须基于真实订单数据回答。
     */
    private String enrichLlmContext(Long userId, String userMessage, String baseContext) {
        StringBuilder contextBuilder = new StringBuilder();
        if (StringUtils.hasText(baseContext)) {
            contextBuilder.append(baseContext.trim());
        }

        String orderToken = customerAiIntentClassifier.extractOrderToken(userMessage);
        if (!StringUtils.hasText(orderToken)) {
            return contextBuilder.toString();
        }

        OrderStatusVO orderStatus = orderTools.queryOrderStatusSilently(userId, orderToken);
        if (orderStatus == null) {
            return contextBuilder.toString();
        }

        if (contextBuilder.length() > 0) {
            contextBuilder.append("\n\n");
        }
        contextBuilder.append("当前用户消息中识别到订单号：").append(orderToken).append("\n")
                .append("订单真实摘要：")
                .append("订单号=").append(orderStatus.getOrderNumber())
                .append("，订单状态=").append(orderStatus.getStatusLabel())
                .append("，支付状态=").append(orderStatus.getPayStatusLabel())
                .append("，订单金额=").append(orderStatus.getAmount())
                .append("，下单时间=").append(orderStatus.getOrderTime())
                .append("，预计送达时间=").append(orderStatus.getEstimatedDeliveryTime())
                .append("，实际送达时间=").append(orderStatus.getDeliveryTime())
                .append("。请严格基于以上真实订单信息回答，不得臆造不存在的退款、配送或库存结果。");
        return contextBuilder.toString();
    }

    /**
     * 将配送范围评估补充为门店场景的结构化上下文。
     */
    private String buildDeliveryScopeContext(DeliveryScopeCheckVO deliveryScope) {
        if (deliveryScope == null || !StringUtils.hasText(deliveryScope.getMessage())) {
            return "";
        }
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("；配送范围评估=").append(deliveryScope.getScopeStatus())
                .append("，评估说明=").append(deliveryScope.getMessage());
        if (deliveryScope.getDistanceMeters() != null) {
            contextBuilder.append("，路线距离=").append(deliveryScope.getDistanceMeters()).append("米");
        }
        return contextBuilder.toString();
    }

    /**
     * 门店场景规则回复，直接体现营业状态和配送范围评估，避免回答过于空泛。
     */
    private String buildStoreAnswer(StoreStatusVO store, DeliveryScopeCheckVO deliveryScope) {
        StringBuilder answer = new StringBuilder();
        answer.append("我帮你查到门店当前").append(store.getStatusLabel()).append("。").append(store.getMessage());
        if (deliveryScope == null || !StringUtils.hasText(deliveryScope.getMessage())) {
            return answer.toString();
        }
        if ("supported".equals(deliveryScope.getScopeStatus())) {
            answer.append(" 按当前地址测算，大概率在配送范围内");
        } else if ("unsupported".equals(deliveryScope.getScopeStatus())) {
            answer.append(" 按当前地址测算，可能已经超出配送范围");
        } else {
            answer.append(" 当前还不能自动确认是否在配送范围内");
        }
        if (deliveryScope.getDistanceMeters() != null) {
            answer.append("，路线距离约 ").append(deliveryScope.getDistanceMeters()).append(" 米");
        }
        answer.append("。").append(deliveryScope.getMessage());
        return answer.toString();
    }

    private AiChatVO buildFallbackReply(Long conversationId) {
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.FALLBACK);
        vo.setAnswer("我暂时没有足够信息直接回答这个问题。你可以补充订单号、商品名，或改问配送、售后、优惠券、门店营业时间这类问题；必要时建议转人工客服。");
        return vo;
    }

    private AiChatVO buildActionReply(Long conversationId, String actionType, String message) {
        AiChatVO vo = new AiChatVO();
        vo.setConversationId(conversationId);
        vo.setRoute(AiRouteType.ACTION);
        vo.setActionType(actionType);
        vo.setAnswer(message);
        return vo;
    }

    private void saveMessage(Long conversationId, Long userId, String role, String content, String contentType, String toolName, String actionType, String toolResult) {
        AiMessage aiMessage = new AiMessage();
        aiMessage.setConversationId(conversationId);
        aiMessage.setUserId(userId);
        aiMessage.setRole(role);
        aiMessage.setContent(content);
        aiMessage.setContentType(contentType);
        aiMessage.setToolName(toolName);
        aiMessage.setActionType(actionType);
        aiMessage.setToolResult(toolResult);
        aiMessage.setAuditStatus(0);
        aiMessage.setAuditRemark(null);
        aiMessage.setCreateTime(LocalDateTime.now());
        aiMessageMapper.insert(aiMessage);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从售后消息里提取订单号之后的自然语言描述，作为售后原因
     */
    private String extractAfterSaleReason(String message) {
        if (!StringUtils.hasText(message)) {
            return null;
        }
        String normalized = message.replace(ACTION_CONFIRM_TOKEN, " ")
                .replace("售后", " ")
                .replace("申请", " ")
                .replace("退款", " ")
                .replace("订单", " ")
                .replaceAll("\\d{1,20}", " ")
                .trim();
        return StringUtils.hasText(normalized) ? normalized : null;
    }
}
