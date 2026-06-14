package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiChatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long conversationId;

    private String answer;

    private String route;

    private String actionType;

    private List<String> references;
}
