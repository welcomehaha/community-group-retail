package com.community.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductCategoryDTO implements Serializable {

    //主键
    private Long id;

    //类型 1 商品分类 2 组合商品分类
    @NotNull(message = "分类类型不能为空")
    private Integer type;

    //分类名称
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 2, max = 20, message = "分类名称请输入2-20个字符")
    @Pattern(regexp = "^[A-Za-z\\u4e00-\\u9fa5]+$", message = "分类名称只能包含中文或英文字母")
    private String name;

    //排序
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序只能输入0-99数字")
    @Max(value = 99, message = "排序只能输入0-99数字")
    private Integer sort;

}
