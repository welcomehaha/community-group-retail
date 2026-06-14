package com.community.ai.service;

import com.community.ai.rag.KnowledgeMatchResult;

public interface CustomerAiKnowledgeService {

    KnowledgeMatchResult match(String question);

    void rebuildKnowledgeIndex();

    void rebuildKnowledgeIndex(Long documentId);
}
