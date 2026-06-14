package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险审计处理更新参数
 */
@Data
public class AiRiskAuditUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI 助手消息ID
     */
    @NotNull(message = "AI助手消息ID不能为空")
    private Long assistantMessageId;

    /**
     * 处理状态：1已确认 2已忽略
     */
    @NotNull(message = "审计状态不能为空")
    @Min(value = 1, message = "审计状态只允许为1或2")
    @Max(value = 2, message = "审计状态只允许为1或2")
    private Integer auditStatus;

    /**
     * 处理备注
     */
    @NotBlank(message = "审计备注不能为空")
    @Size(max = 200, message = "审计备注不能超过200字")
    private String auditRemark;
}
