package com.community.ai.router;

import com.community.ai.tool.OrderTools;
import com.community.ai.tool.StoreTools;
import com.community.ai.tool.ActivityTools;
import com.community.ai.tool.DeliveryScopeTools;
import com.community.ai.tool.UserProfileTools;
import com.community.vo.ActivityKnowledgeVO;
import com.community.vo.DeliveryScopeCheckVO;
import com.community.vo.OrderStatusVO;
import com.community.vo.StoreStatusVO;
import com.community.vo.UserDeliveryProfileVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 组装用户当前业务状态，用于增强场景回答的一致性和任务链上下文。
 */
@Component
public class CustomerAiUserStateAssembler {

    private final OrderTools orderTools;
    private final StoreTools storeTools;
    private final ActivityTools activityTools;
    private final UserProfileTools userProfileTools;
    private final DeliveryScopeTools deliveryScopeTools;

    public CustomerAiUserStateAssembler(OrderTools orderTools,
                                        StoreTools storeTools,
                                        ActivityTools activityTools,
                                        UserProfileTools userProfileTools,
                                        DeliveryScopeTools deliveryScopeTools) {
        this.orderTools = orderTools;
        this.storeTools = storeTools;
        this.activityTools = activityTools;
        this.userProfileTools = userProfileTools;
        this.deliveryScopeTools = deliveryScopeTools;
    }

    /**
     * 根据路由补充可复用的用户状态摘要。
     */
    public String buildStateContext(Long userId, Long conversationId, AiIntentAnalysis intentAnalysis) {
        if (intentAnalysis == null || !StringUtils.hasText(intentAnalysis.getRoute())) {
            return "";
        }

        String route = intentAnalysis.getRoute();
        if ("order".equals(route) || "action".equals(route)) {
            return buildOrderStateContext(userId, conversationId, intentAnalysis.getOrderToken());
        }
        if ("store".equals(route)) {
            return buildStoreStateContext(userId, conversationId);
        }
        if ("activity".equals(route)) {
            return buildActivityStateContext(userId, conversationId, intentAnalysis.getProductKeyword());
        }
        return "";
    }

    private String buildOrderStateContext(Long userId, Long conversationId, String orderToken) {
        OrderStatusVO orderStatus = StringUtils.hasText(orderToken)
                ? orderTools.queryOrderStatusSilently(userId, orderToken)
                : orderTools.queryLatestOrder(userId, conversationId);
        if (orderStatus == null) {
            return "";
        }
        return new StringBuilder()
                .append("用户当前关联订单摘要：")
                .append("订单号=").append(orderStatus.getOrderNumber())
                .append("，订单状态=").append(orderStatus.getStatusLabel())
                .append("，支付状态=").append(orderStatus.getPayStatusLabel())
                .append("，订单金额=").append(orderStatus.getAmount())
                .append("，下单时间=").append(orderStatus.getOrderTime())
                .append("，预计送达时间=").append(orderStatus.getEstimatedDeliveryTime())
                .append("，实际送达时间=").append(orderStatus.getDeliveryTime())
                .toString();
    }

    private String buildStoreStateContext(Long userId, Long conversationId) {
        StoreStatusVO storeStatus = storeTools.queryStoreStatus(userId, conversationId);
        UserDeliveryProfileVO deliveryProfile = userProfileTools.queryUserDeliveryProfile(userId, conversationId);
        DeliveryScopeCheckVO deliveryScope = deliveryScopeTools.checkDeliveryScope(userId, conversationId);
        if (storeStatus == null && deliveryProfile == null && deliveryScope == null) {
            return "";
        }
        StringBuilder contextBuilder = new StringBuilder();
        if (storeStatus != null) {
            contextBuilder.append("门店当前状态：").append(storeStatus.getStatusLabel()).append("；说明：").append(storeStatus.getMessage());
        }
        if (deliveryProfile != null) {
            if (contextBuilder.length() > 0) {
                contextBuilder.append("；");
            }
            if (deliveryProfile.getDefaultAddress() != null) {
                contextBuilder.append("用户默认收货地址：").append(deliveryProfile.getDefaultAddress());
            }
            if (deliveryProfile.getRecentOrderAddress() != null) {
                contextBuilder.append("；最近订单地址：").append(deliveryProfile.getRecentOrderAddress());
            }
        }
        if (deliveryScope != null) {
            if (contextBuilder.length() > 0) {
                contextBuilder.append("；");
            }
            contextBuilder.append("配送范围评估：").append(deliveryScope.getScopeStatus())
                    .append("，说明：").append(deliveryScope.getMessage());
            if (deliveryScope.getDistanceMeters() != null) {
                contextBuilder.append("，路线距离约 ").append(deliveryScope.getDistanceMeters()).append(" 米");
            }
        }
        return contextBuilder.toString();
    }

    private String buildActivityStateContext(Long userId, Long conversationId, String keyword) {
        ActivityKnowledgeVO activityKnowledge = activityTools.queryActivityKnowledge(userId, conversationId, keyword);
        if (activityKnowledge == null) {
            return "";
        }
        return "活动知识摘要：" + activityKnowledge.getTitle() + "；内容：" + activityKnowledge.getContent();
    }
}
