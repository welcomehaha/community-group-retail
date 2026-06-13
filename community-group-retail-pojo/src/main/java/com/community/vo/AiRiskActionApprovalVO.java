package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险动作审批通过率项
 */
@Data
public class AiRiskActionApprovalVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动作类型
     */
    private String actionType;

    /**
     * 审批总数
     */
    private Long approvalTotalCount;

    /**
     * 审批通过数
     */
    private Long approvalApprovedCount;

    /**
     * 审批通过率
     */
    private Double approvalPassRate;
}
