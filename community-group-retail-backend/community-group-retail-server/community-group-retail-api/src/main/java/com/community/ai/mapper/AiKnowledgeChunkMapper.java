package com.community.ai.mapper;

import com.community.entity.AiKnowledgeChunk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiKnowledgeChunkMapper {

    List<AiKnowledgeChunk> listByDocumentId(Long documentId);

    void batchInsert(@Param("chunks") List<AiKnowledgeChunk> chunks);

    void deleteByDocumentId(Long documentId);
}
