package com.community.ai.mapper;

import com.community.entity.AiAfterSaleRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiAfterSaleRequestMapper {

    /**
     * 新增 AI 售后申请记录
     *
     * @param request 售后申请记录
     */
    void insert(AiAfterSaleRequest request);
}
