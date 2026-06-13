package com.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiKnowledgeDocumentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String docType;

    private String sourceType;

    private Long sourceId;

    private String content;

    private Integer status;
}
