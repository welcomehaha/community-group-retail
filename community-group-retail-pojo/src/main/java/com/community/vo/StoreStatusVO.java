package com.community.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;

    private String statusLabel;

    private String message;
}
