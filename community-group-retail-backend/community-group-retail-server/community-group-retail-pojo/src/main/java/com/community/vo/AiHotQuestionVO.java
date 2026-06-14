package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 热门问题统计
 */
@Data
public class AiHotQuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String question;

    private Integer hitCount;
}
