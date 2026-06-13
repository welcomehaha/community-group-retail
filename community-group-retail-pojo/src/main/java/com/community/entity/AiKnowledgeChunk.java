package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiKnowledgeChunk implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long documentId;

    private Integer chunkIndex;

    private String chunkText;

    private String vectorDocId;

    private LocalDateTime createTime;
}
