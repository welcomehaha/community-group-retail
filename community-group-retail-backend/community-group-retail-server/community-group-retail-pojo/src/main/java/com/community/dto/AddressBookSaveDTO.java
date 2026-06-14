package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增收货地址请求对象。
 */
@Data
public class AddressBookSaveDTO implements Serializable {

    /**
     * 收货人姓名。
     */
    @NotBlank(message = "收货人不能为空")
    @Size(min = 2, max = 12, message = "收货人请输入2-12个字符")
    @Pattern(regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+$", message = "收货人只能包含中文、英文或数字")
    private String consignee;

    /**
     * 联系电话。
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号输入有误")
    private String phone;

    /**
     * 性别：0 女，1 男。
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^[01]$", message = "性别参数不合法")
    private String sex;

    /**
     * 省级行政区编码。
     */
    @NotBlank(message = "省份编码不能为空")
    private String provinceCode;

    /**
     * 省级名称。
     */
    @NotBlank(message = "省份名称不能为空")
    private String provinceName;

    /**
     * 市级行政区编码。
     */
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    /**
     * 市级名称。
     */
    @NotBlank(message = "城市名称不能为空")
    private String cityName;

    /**
     * 区县行政区编码。
     */
    @NotBlank(message = "区县编码不能为空")
    private String districtCode;

    /**
     * 区县名称。
     */
    @NotBlank(message = "区县名称不能为空")
    private String districtName;

    /**
     * 详细地址。
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 100, message = "详细地址不能超过100个字符")
    private String detail;

    /**
     * 地址标签。
     */
    @NotBlank(message = "地址标签不能为空")
    @Pattern(regexp = "^[1-3]$", message = "地址标签参数不合法")
    private String label;
}
