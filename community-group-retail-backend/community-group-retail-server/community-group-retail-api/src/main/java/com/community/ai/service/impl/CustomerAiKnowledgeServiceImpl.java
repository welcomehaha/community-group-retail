package com.community.ai.service.impl;

import com.community.ai.mapper.AiKnowledgeChunkMapper;
import com.community.ai.mapper.AiKnowledgeDocumentMapper;
import com.community.ai.rag.KnowledgeMatchResult;
import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.entity.AiKnowledgeChunk;
import com.community.entity.AiKnowledgeDocument;
import com.community.properties.AiProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerAiKnowledgeServiceImpl implements CustomerAiKnowledgeService {

    private final AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;
    private final AiKnowledgeChunkMapper aiKnowledgeChunkMapper;
    private final VectorStore customerAiVectorStore;
    private final AiProperties aiProperties;

    public CustomerAiKnowledgeServiceImpl(AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper,
                                          AiKnowledgeChunkMapper aiKnowledgeChunkMapper,
                                          ObjectProvider<VectorStore> customerAiVectorStoreProvider,
                                          AiProperties aiProperties) {
        this.aiKnowledgeDocumentMapper = aiKnowledgeDocumentMapper;
        this.aiKnowledgeChunkMapper = aiKnowledgeChunkMapper;
        this.customerAiVectorStore = customerAiVectorStoreProvider.getIfAvailable();
        this.aiProperties = aiProperties;
    }

    @Override
    public KnowledgeMatchResult match(String question) {
        if (!StringUtils.hasText(question)) {
            return null;
        }
        if (isVectorSearchAvailable()) {
            List<Document> similarDocuments = customerAiVectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(question)
                            .topK(aiProperties.getTopK())
                            .similarityThreshold(aiProperties.getSimilarityThreshold())
                            .build()
            );
            if (similarDocuments != null && !similarDocuments.isEmpty()) {
                Document document = similarDocuments.get(0);
                KnowledgeMatchResult result = new KnowledgeMatchResult();
                Object documentId = document.getMetadata().get("documentId");
                result.setDocumentId(documentId == null ? null : Long.valueOf(String.valueOf(documentId)));
                result.setTitle(String.valueOf(document.getMetadata().getOrDefault("title", "知识库文档")));
                result.setContent(document.getText());
                result.setScore(100);
                return result;
            }
        }

        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentMapper.listEnabled();
        if (documents == null || documents.isEmpty()) {
            return null;
        }

        String[] terms = normalize(question).split("\\s+");
        List<KnowledgeMatchResult> matches = new ArrayList<>();
        for (AiKnowledgeDocument document : documents) {
            String haystack = normalize(document.getTitle() + " " + document.getContent());
            int score = 0;
            for (String term : terms) {
                if (StringUtils.hasText(term) && haystack.contains(term)) {
                    score++;
                }
            }
            if (score > 0) {
                KnowledgeMatchResult result = new KnowledgeMatchResult();
                result.setDocumentId(document.getId());
                result.setTitle(document.getTitle());
                result.setContent(document.getContent());
                result.setScore(score);
                matches.add(result);
            }
        }
        return matches.stream().max(Comparator.comparingInt(KnowledgeMatchResult::getScore)).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildKnowledgeIndex() {
        if (!isVectorSearchAvailable()) {
            return;
        }
        resetVectorStore();
        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentMapper.listEnabled();
        if (documents == null || documents.isEmpty()) {
            return;
        }
        for (AiKnowledgeDocument document : documents) {
            rebuildKnowledgeIndex(document.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildKnowledgeIndex(Long documentId) {
        if (documentId == null || !isVectorSearchAvailable()) {
            return;
        }
        AiKnowledgeDocument document = aiKnowledgeDocumentMapper.getById(documentId);
        aiKnowledgeChunkMapper.deleteByDocumentId(documentId);
        if (document == null || document.getStatus() == null || document.getStatus() != 1 || !StringUtils.hasText(document.getContent())) {
            return;
        }
        List<Document> vectorDocuments = buildVectorDocuments(document);
        if (vectorDocuments.isEmpty()) {
            return;
        }
        customerAiVectorStore.add(vectorDocuments);
        aiKnowledgeChunkMapper.batchInsert(buildKnowledgeChunks(documentId, vectorDocuments));
    }

    /**
     * 按句段切分知识文本，兼顾 RAG 检索粒度和语义完整性
     */
    private List<Document> buildVectorDocuments(AiKnowledgeDocument document) {
        List<String> segments = splitContent(document.getContent());
        List<Document> vectorDocuments = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            String vectorDocId = buildVectorDocId(document.getId(), i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("documentId", document.getId());
            metadata.put("title", document.getTitle());
            metadata.put("docType", document.getDocType());
            metadata.put("sourceType", document.getSourceType());
            metadata.put("chunkIndex", i);
            vectorDocuments.add(Document.builder()
                    .id(vectorDocId)
                    .text(segments.get(i))
                    .metadata(metadata)
                    .build());
        }
        return vectorDocuments;
    }

    /**
     * 同步落库知识分片，便于后台排查命中结果及后续升级持久化向量库
     */
    private List<AiKnowledgeChunk> buildKnowledgeChunks(Long documentId, List<Document> vectorDocuments) {
        List<AiKnowledgeChunk> chunks = new ArrayList<>();
        for (int i = 0; i < vectorDocuments.size(); i++) {
            Document vectorDocument = vectorDocuments.get(i);
            AiKnowledgeChunk chunk = new AiKnowledgeChunk();
            chunk.setDocumentId(documentId);
            chunk.setChunkIndex(i);
            chunk.setChunkText(vectorDocument.getText());
            chunk.setVectorDocId(vectorDocument.getId());
            chunk.setCreateTime(LocalDateTime.now());
            chunks.add(chunk);
        }
        return chunks;
    }

    /**
     * 使用轻量中文分句规则做切片，避免整篇文档作为单个向量影响召回质量
     */
    private List<String> splitContent(String content) {
        List<String> segments = new ArrayList<>();
        String normalized = content == null ? "" : content.replace("\r", "\n");
        String[] paragraphs = normalized.split("\\n+");
        for (String paragraph : paragraphs) {
            if (!StringUtils.hasText(paragraph)) {
                continue;
            }
            String[] sentences = paragraph.split("(?<=[。！？；.!?;])");
            StringBuilder current = new StringBuilder();
            for (String sentence : sentences) {
                String trimmed = sentence == null ? "" : sentence.trim();
                if (!StringUtils.hasText(trimmed)) {
                    continue;
                }
                if (current.length() + trimmed.length() > 220 && current.length() > 0) {
                    segments.add(current.toString());
                    current = new StringBuilder();
                }
                current.append(trimmed);
            }
            if (current.length() > 0) {
                segments.add(current.toString());
            }
        }
        if (segments.isEmpty() && StringUtils.hasText(content)) {
            segments.add(content.trim());
        }
        return segments;
    }

    /**
     * 生成稳定的向量文档 ID，便于知识文档更新时执行先删后建
     */
    private String buildVectorDocId(Long documentId, int chunkIndex) {
        return "knowledge-" + documentId + "-" + chunkIndex;
    }

    /**
     * SimpleVectorStore 作为内存向量库时，重建前先清空旧索引，避免重复写入
     */
    private void resetVectorStore() {
        if (customerAiVectorStore == null) {
            return;
        }
        if (customerAiVectorStore instanceof SimpleVectorStore simpleVectorStore) {
            simpleVectorStore.getNativeClient().ifPresent(nativeStore -> {
                if (nativeStore instanceof Map<?, ?> store) {
                    List<String> ids = new ArrayList<>();
                    for (Object key : store.keySet()) {
                        ids.add(String.valueOf(key));
                    }
                    if (!ids.isEmpty()) {
                        customerAiVectorStore.delete(ids);
                    }
                }
            });
        }
    }

    private String normalize(String text) {
        return text == null ? "" : text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsIdeographic}]+", " ").toLowerCase();
    }

    /**
     * 只有显式启用 Spring AI 且成功装配向量库时，才执行向量检索与索引构建
     */
    private boolean isVectorSearchAvailable() {
        return Boolean.TRUE.equals(aiProperties.getEnabled()) && customerAiVectorStore != null;
    }
}
