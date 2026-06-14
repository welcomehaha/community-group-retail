package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AI 高风险审批概览
 */
@Data
public class AiRiskApprovalOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审批总数
     */
    private Long approvalTotalCount;

    /**
     * 审批通过数
     */
    private Long approvalApprovedCount;

    /**
     * 审批驳回数
     */
    private Long approvalRejectedCount;

    /**
     * 审批通过率
     */
    private BigDecimal approvalPassRate;
}
