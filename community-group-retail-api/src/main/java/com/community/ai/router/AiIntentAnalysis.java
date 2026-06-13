package com.community.ai.router;

import lombok.Builder;
import lombok.Data;

/**
 * AI 意图分析结果，统一承载路由分类和从自然语言中提取出的关键槽位。
 */
@Data
@Builder
public class AiIntentAnalysis {

    /**
     * 一级路由类型，和 AiChatVO.route 保持一致。
     */
    private String route;

    /**
     * 动作类型，命中真实业务写操作时返回。
     */
    private String actionType;

    /**
     * 是否为需要显式确认的高风险动作。
     */
    private boolean riskyAction;

    /**
     * 从用户消息中提取到的订单号或订单主键。
     */
    private String orderToken;

    /**
     * 从用户消息中提取到的商品关键词。
     */
    private String productKeyword;

    /**
     * 是否命中了活动、优惠券等规则咨询场景。
     */
    private boolean activityIntent;
}
