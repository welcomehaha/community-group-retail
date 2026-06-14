package com.community.ai.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiLlmReply implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;

    private String content;

    private String errorMessage;
}
