package com.community.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 高风险审计分页查询参数
 */
@Data
public class AiRiskAuditPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 动作类型：cancel_order、remind_order、after_sale
     */
    private String actionType;

    /**
     * 审计状态：0待处理 1已确认 2已忽略
     */
    private Integer auditStatus;

    /**
     * 审批状态：0待审批 1已通过 2已驳回
     */
    private Integer approvalStatus;

    /**
     * 审批人ID
     */
    private Long approvalUserId;

    /**
     * 用户问题关键词
     */
    private String questionKeyword;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 审批开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalBeginTime;

    /**
     * 审批结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalEndTime;
}
