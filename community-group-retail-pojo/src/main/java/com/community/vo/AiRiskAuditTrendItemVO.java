package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险审计趋势项
 */
@Data
public class AiRiskAuditTrendItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String statDate;

    /**
     * 审批通过数
     */
    private Long approvalApprovedCount;

    /**
     * 审批驳回数
     */
    private Long approvalRejectedCount;

    /**
     * 高风险命中数
     */
    private Long riskHitCount;

    /**
     * 审批通过率
     */
    private Double approvalPassRate;
}
