package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 活动知识检索结果，给 AI 活动场景提供结构化上下文。
 */
@Data
public class ActivityKnowledgeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long documentId;

    private String title;

    private String docType;

    private String content;

    /**
     * 是否与当前订单金额相关，便于提示模型做门槛解释。
     */
    private Boolean orderRelated;
}
