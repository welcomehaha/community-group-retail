package com.community.ai.router;

import com.community.ai.enums.AiRouteType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 统一做用户问题分类，避免业务服务中散落大量关键词判断。
 */
@Component
public class CustomerAiIntentClassifier {

    private static final Pattern ORDER_TOKEN_PATTERN = Pattern.compile("(\\d{1,32})");

    private static final String ACTION_TYPE_REMIND_ORDER = "remind_order";
    private static final String ACTION_TYPE_CANCEL_ORDER = "cancel_order";
    private static final String ACTION_TYPE_AFTER_SALE = "after_sale";

    /**
     * 识别用户意图，并抽取订单号、商品关键词等基础槽位。
     */
    public AiIntentAnalysis classify(String message) {
        String normalizedMessage = message == null ? "" : message.trim();
        String lowerMessage = normalizedMessage.toLowerCase();
        String orderToken = extractOrderToken(normalizedMessage);

        if (containsAny(lowerMessage, "售后", "退款", "破损", "缺货", "错发", "漏发", "变质")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.ACTION)
                    .actionType(ACTION_TYPE_AFTER_SALE)
                    .riskyAction(true)
                    .orderToken(orderToken)
                    .build();
        }

        if (containsAny(lowerMessage, "取消订单", "取消")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.ACTION)
                    .actionType(ACTION_TYPE_CANCEL_ORDER)
                    .riskyAction(true)
                    .orderToken(orderToken)
                    .build();
        }

        if (containsAny(lowerMessage, "催单", "提醒商家", "加急")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.ACTION)
                    .actionType(ACTION_TYPE_REMIND_ORDER)
                    .riskyAction(true)
                    .orderToken(orderToken)
                    .build();
        }

        if (containsAny(lowerMessage, "订单", "配送", "物流", "骑手", "送达")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.ORDER)
                    .orderToken(orderToken)
                    .build();
        }

        if (containsAny(lowerMessage, "营业", "门店", "打烊", "开门", "配送范围", "营业时间")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.STORE)
                    .build();
        }

        if (containsAny(lowerMessage, "商品", "菜品", "可买", "上架", "库存", "价格", "多少钱")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.PRODUCT)
                    .productKeyword(extractProductKeyword(normalizedMessage))
                    .build();
        }

        if (containsAny(lowerMessage, "优惠券", "满减", "活动", "折扣", "红包", "新人", "会员")) {
            return AiIntentAnalysis.builder()
                    .route(AiRouteType.ACTIVITY)
                    .activityIntent(true)
                    .build();
        }

        return AiIntentAnalysis.builder()
                .route(AiRouteType.FAQ)
                .orderToken(orderToken)
                .productKeyword(extractProductKeyword(normalizedMessage))
                .build();
    }

    /**
     * 提取商品关键词，供商品场景复用。
     */
    public String extractProductKeyword(String message) {
        if (!StringUtils.hasText(message)) {
            return null;
        }
        String normalized = message
                .replace("商品", " ")
                .replace("菜品", " ")
                .replace("库存", " ")
                .replace("上架", " ")
                .replace("可买", " ")
                .replace("能买", " ")
                .replace("价格", " ")
                .replace("多少钱", " ")
                .replace("吗", " ")
                .replace("是否", " ")
                .replace("查询", " ")
                .trim();
        return StringUtils.hasText(normalized) ? normalized : null;
    }

    /**
     * 提取订单号或订单主键，兼容用户直接粘贴数字。
     */
    public String extractOrderToken(String message) {
        Matcher matcher = ORDER_TOKEN_PATTERN.matcher(message == null ? "" : message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
