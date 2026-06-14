package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 未命中问题分析
 */
@Data
public class AiFallbackAnalysisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String question;

    private Integer hitCount;
}
