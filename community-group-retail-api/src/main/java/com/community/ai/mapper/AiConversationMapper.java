package com.community.ai.mapper;

import com.community.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiConversationMapper {

    void insert(AiConversation conversation);

    AiConversation getById(Long id);

    AiConversation getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<AiConversation> listByUserId(Long userId);

    List<AiConversation> listAll();

    void updateTitle(@Param("id") Long id, @Param("title") String title);

    void touch(Long id);

    void deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
