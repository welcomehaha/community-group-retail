package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiConversation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String conversationType;

    private String title;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
