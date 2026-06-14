package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiKnowledgeDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String docType;

    private String sourceType;

    private Long sourceId;

    private String content;

    private Integer status;

    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
