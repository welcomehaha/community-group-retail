package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 高风险动作审批更新参数
 */
@Data
public class AiRiskApprovalUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI 助手消息ID
     */
    @NotNull(message = "AI助手消息ID不能为空")
    private Long assistantMessageId;

    /**
     * 审批状态：1已通过 2已驳回
     */
    @NotNull(message = "审批状态不能为空")
    @Min(value = 1, message = "审批状态只允许为1或2")
    @Max(value = 2, message = "审批状态只允许为1或2")
    private Integer approvalStatus;

    /**
     * 审批备注
     */
    @NotBlank(message = "审批备注不能为空")
    @Size(max = 200, message = "审批备注不能超过200字")
    private String approvalRemark;
}
