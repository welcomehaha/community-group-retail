package com.community.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.community.entity.ProductBundleItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductBundleDTO implements Serializable {

    private Long id;

    //分类id
    @NotNull(message = "请选择组合商品分类")
    private Long categoryId;

    //组合商品名称
    @NotBlank(message = "请输入组合商品名称")
    @Size(min = 2, max = 20, message = "组合商品名称输入不符，请输入2-20个字符")
    @Pattern(regexp = "^(家庭早餐组合|蔬菜优选组合|火锅食材组合|一周水果组合|居家清洁组合)$", message = "建议使用家庭早餐组合、蔬菜优选组合等社区场景组合名称")
    private String name;

    //组合商品价格
    @NotNull(message = "请输入组合商品价格")
    @DecimalMin(value = "0.01", message = "组合商品价格必须大于0")
    @Digits(integer = 6, fraction = 2, message = "组合商品价格最多6位整数且保留2位小数")
    private BigDecimal price;

    //状态 0:下架 1:上架
    private Integer status;

    //描述信息
    @Size(max = 200, message = "组合商品描述最长200字")
    private String description;

    //图片
    @NotBlank(message = "组合商品图片不能为空")
    private String image;

    //组合商品明细关系
    @Size(min = 2, message = "组合商品至少需要选择2个单品")
    @JsonAlias("setmealDishes")
    private List<ProductBundleItem> productBundleItems = new ArrayList<>();

}
