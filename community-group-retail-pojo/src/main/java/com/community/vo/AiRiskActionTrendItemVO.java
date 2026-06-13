package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险动作类型趋势项
 */
@Data
public class AiRiskActionTrendItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String statDate;

    /**
     * 取消订单数
     */
    private Long cancelOrderCount;

    /**
     * 催单数
     */
    private Long remindOrderCount;

    /**
     * 售后申请数
     */
    private Long afterSaleCount;
}
