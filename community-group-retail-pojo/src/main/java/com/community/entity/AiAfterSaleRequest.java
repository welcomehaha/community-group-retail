package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 售后申请记录
 */
@Data
public class AiAfterSaleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderId;

    private Long userId;

    private Long conversationId;

    private String requestType;

    private String reason;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
