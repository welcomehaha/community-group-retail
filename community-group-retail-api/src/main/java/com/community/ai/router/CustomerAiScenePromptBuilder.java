package com.community.ai.router;

import com.community.ai.enums.AiRouteType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 根据场景动态构建系统提示词，避免所有任务共用一份泛化提示词。
 */
@Component
public class CustomerAiScenePromptBuilder {

    /**
     * 为不同任务返回更聚焦的系统提示词。
     */
    public String buildSystemPrompt(String basePrompt, String route, String context) {
        StringBuilder prompt = new StringBuilder();
        if (StringUtils.hasText(basePrompt)) {
            prompt.append(basePrompt.trim());
        }

        if (prompt.length() > 0) {
            prompt.append("\n\n");
        }

        prompt.append("你必须优先执行当前路由场景的回答规则。");

        if (AiRouteType.ORDER.equals(route)) {
            prompt.append("\n- 当前是订单场景，只能基于订单真实状态、支付状态、配送时间回答。");
            prompt.append("\n- 不要臆造退款结果、骑手位置、配送承诺。");
        } else if (AiRouteType.PRODUCT.equals(route)) {
            prompt.append("\n- 当前是商品场景，只能基于商品名称、价格、起售状态、描述回答。");
            prompt.append("\n- 不要推断不存在的库存和促销信息。");
        } else if (AiRouteType.STORE.equals(route)) {
            prompt.append("\n- 当前是门店场景，只能基于营业状态、配送范围、营业时间回答。");
        } else if (AiRouteType.ACTIVITY.equals(route)) {
            prompt.append("\n- 当前是活动规则场景，优先使用知识库内容归纳说明优惠券、满减、活动规则。");
            prompt.append("\n- 若知识库未提供规则，不要编造活动门槛和有效期。");
        } else if (AiRouteType.FAQ.equals(route)) {
            prompt.append("\n- 当前是知识问答场景，优先基于知识库上下文作答。");
        } else if (AiRouteType.ACTION.equals(route)) {
            prompt.append("\n- 当前是高风险动作场景，只允许解释执行条件、确认要求和执行结果。");
            prompt.append("\n- 不要绕过显式确认，不要自行放宽权限和状态限制。");
        }

        if (StringUtils.hasText(context)) {
            prompt.append("\n\n当前业务上下文如下：\n").append(context.trim());
        }
        return prompt.toString();
    }
}
