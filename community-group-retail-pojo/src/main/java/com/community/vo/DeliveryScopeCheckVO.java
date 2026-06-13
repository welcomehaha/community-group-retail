package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 配送范围评估结果。
 */
@Data
public class DeliveryScopeCheckVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * supported / unsupported / unknown
     */
    private String scopeStatus;

    /**
     * 评估结果说明。
     */
    private String message;

    /**
     * 当成功完成路线规划时，返回距离，单位米。
     */
    private Integer distanceMeters;

    /**
     * 用于判定的收货地址。
     */
    private String targetAddress;
}
