package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String role;

    private String content;

    private String contentType;

    private String toolName;

    private String actionType;

    private String toolResult;

    private LocalDateTime createTime;
}
