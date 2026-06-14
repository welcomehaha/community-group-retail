package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiConversationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long conversationId;

    private String title;

    private Integer status;

    private String lastMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
