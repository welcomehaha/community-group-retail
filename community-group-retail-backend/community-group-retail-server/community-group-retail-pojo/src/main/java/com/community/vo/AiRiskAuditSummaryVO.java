package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险审计状态统计
 */
@Data
public class AiRiskAuditSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全部数量
     */
    private Long totalCount;

    /**
     * 待处理数量
     */
    private Long pendingCount;

    /**
     * 已确认数量
     */
    private Long confirmedCount;

    /**
     * 已忽略数量
     */
    private Long ignoredCount;

    /**
     * 待审批数量
     */
    private Long approvalPendingCount;

    /**
     * 已通过数量
     */
    private Long approvalApprovedCount;

    /**
     * 已驳回数量
     */
    private Long approvalRejectedCount;
}
