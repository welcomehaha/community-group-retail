package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 未命中问题趋势项
 */
@Data
public class AiFallbackTrendItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String statDate;

    /**
     * 未命中次数
     */
    private Long fallbackCount;
}
