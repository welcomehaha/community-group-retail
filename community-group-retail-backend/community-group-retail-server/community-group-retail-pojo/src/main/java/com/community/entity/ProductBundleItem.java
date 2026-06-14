package com.community.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐菜品关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBundleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //组合商品id
    @JsonAlias("setmealId")
    private Long productBundleId;

    //商品id
    @JsonAlias("dishId")
    private Long productId;

    //菜品名称 （冗余字段）
    private String name;

    //菜品原价
    private BigDecimal price;

    //份数
    private Integer copies;
}
