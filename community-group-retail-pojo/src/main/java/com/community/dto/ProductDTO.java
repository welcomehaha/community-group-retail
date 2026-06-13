package com.community.dto;

import com.community.entity.ProductSpec;
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
public class ProductDTO implements Serializable {

    private Long id;
    //商品名称
    @NotBlank(message = "请输入商品名称")
    @Size(min = 2, max = 20, message = "商品名称输入不符，请输入2-20个字符")
    @Pattern(regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+$", message = "商品名称只能包含中英文或数字")
    private String name;
    //商品分类id
    @NotNull(message = "请选择商品分类")
    private Long categoryId;
    //商品价格
    @NotNull(message = "请填写商品价格")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 6, fraction = 2, message = "商品价格最多6位整数且保留2位小数")
    private BigDecimal price;
    //图片
    @NotBlank(message = "商品图片不能为空")
    private String image;
    //描述信息
    @Size(max = 200, message = "商品描述最长200字")
    private String description;
    //0 下架 1 上架
    private Integer status;
    //口味
    private List<ProductSpec> flavors = new ArrayList<>();

}
