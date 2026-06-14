package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiToolCallLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long conversationId;

    private Long userId;

    private String toolName;

    private String requestJson;

    private String responseJson;

    private Integer successFlag;

    private String errorMessage;

    private LocalDateTime createTime;
}
