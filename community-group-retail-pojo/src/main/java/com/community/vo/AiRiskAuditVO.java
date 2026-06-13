package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 高风险回答审计记录
 */
@Data
public class AiRiskAuditVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long conversationId;

    private Long userMessageId;

    private Long assistantMessageId;

    private String userQuestion;

    private String assistantReply;

    private String route;

    private String actionType;

    private String riskReason;

    private Integer auditStatus;

    private String auditRemark;

    private Integer approvalStatus;

    private String approvalRemark;

    private Long approvalUserId;

    private LocalDateTime approvalTime;

    private LocalDateTime createTime;
}
