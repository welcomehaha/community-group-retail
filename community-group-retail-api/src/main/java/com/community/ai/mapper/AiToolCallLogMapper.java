package com.community.ai.mapper;

import com.community.entity.AiToolCallLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiToolCallLogMapper {

    void insert(AiToolCallLog log);

    AiToolCallLog getById(Long id);

    java.util.List<AiToolCallLog> listAll();
}
