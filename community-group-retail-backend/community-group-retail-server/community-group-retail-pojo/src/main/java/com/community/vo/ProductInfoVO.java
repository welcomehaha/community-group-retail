package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productName;

    private Integer status;

    private String statusLabel;

    private BigDecimal price;

    private String description;

    private Boolean available;
}
