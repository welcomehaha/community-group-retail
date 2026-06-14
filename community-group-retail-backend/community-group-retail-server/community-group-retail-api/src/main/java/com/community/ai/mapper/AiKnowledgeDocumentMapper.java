package com.community.ai.mapper;

import com.community.entity.AiKnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiKnowledgeDocumentMapper {

    List<AiKnowledgeDocument> listEnabled();

    List<AiKnowledgeDocument> listAll();

    AiKnowledgeDocument getById(Long id);

    /**
     * 按知识类型查询已启用知识，用于活动、售后等垂直场景工具化检索。
     */
    List<AiKnowledgeDocument> listEnabledByDocType(@Param("docType") String docType);

    void insert(AiKnowledgeDocument document);

    void update(AiKnowledgeDocument document);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
