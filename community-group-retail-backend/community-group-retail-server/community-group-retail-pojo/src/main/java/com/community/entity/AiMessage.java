package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long conversationId;

    private Long userId;

    private String role;

    private String content;

    private String contentType;

    private String toolName;

    /**
     * 动作类型：cancel_order、remind_order、after_sale
     */
    private String actionType;

    private String toolResult;

    /**
     * 风险审计处理状态：0待处理 1已确认 2已忽略
     */
    private Integer auditStatus;

    /**
     * 风险审计处理备注
     */
    private String auditRemark;

    /**
     * 审批状态：0待审批 1已通过 2已驳回
     */
    private Integer approvalStatus;

    /**
     * 审批备注
     */
    private String approvalRemark;

    /**
     * 审批人ID
     */
    private Long approvalUserId;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    private LocalDateTime createTime;
}
