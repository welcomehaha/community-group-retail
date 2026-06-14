package com.community.ai.rag;

import lombok.Data;

@Data
public class KnowledgeMatchResult {

    private Long documentId;

    private String title;

    private String content;

    private int score;
}
