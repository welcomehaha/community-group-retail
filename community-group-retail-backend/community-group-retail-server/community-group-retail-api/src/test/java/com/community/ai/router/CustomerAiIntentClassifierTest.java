package com.community.ai.router;

import com.community.ai.enums.AiRouteType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerAiIntentClassifierTest {

    private final CustomerAiIntentClassifier customerAiIntentClassifier = new CustomerAiIntentClassifier();

    @Test
    void shouldClassifyAfterSaleAction() {
        AiIntentAnalysis analysis = customerAiIntentClassifier.classify("确认售后 20240611001 商品破损");

        assertEquals(AiRouteType.ACTION, analysis.getRoute());
        assertEquals("after_sale", analysis.getActionType());
        assertEquals("20240611001", analysis.getOrderToken());
        assertTrue(analysis.isRiskyAction());
    }

    @Test
    void shouldClassifyActivityQuestion() {
        AiIntentAnalysis analysis = customerAiIntentClassifier.classify("新人优惠券可以提现吗");

        assertEquals(AiRouteType.ACTIVITY, analysis.getRoute());
        assertTrue(analysis.isActivityIntent());
    }

    @Test
    void shouldExtractProductKeyword() {
        AiIntentAnalysis analysis = customerAiIntentClassifier.classify("帮我查一下商品 可乐 多少钱");

        assertEquals(AiRouteType.PRODUCT, analysis.getRoute());
        assertEquals("帮我查一下  可乐", analysis.getProductKeyword());
    }
}
