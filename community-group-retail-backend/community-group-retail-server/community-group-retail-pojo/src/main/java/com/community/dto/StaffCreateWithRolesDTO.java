package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建员工并分配角色请求对象。
 */
@Data
public class StaffCreateWithRolesDTO implements Serializable {

    /**
     * 员工账号。
     */
    @NotBlank(message = "员工账号不能为空")
    @Size(min = 4, max = 20, message = "员工账号长度应为4-20位")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "员工账号只能包含字母、数字和下划线")
    private String username;

    /**
     * 员工姓名。
     */
    @NotBlank(message = "员工姓名不能为空")
    @Size(min = 2, max = 20, message = "员工姓名长度应为2-20位")
    @Pattern(regexp = "^[A-Za-z\\u4e00-\\u9fa5]+$", message = "员工姓名只能包含中文或英文字母")
    private String name;

    /**
     * 手机号。
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String phone;

    /**
     * 性别。
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^[12]$", message = "性别参数不合法")
    private String sex;

    /**
     * 身份证号。
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(X|x)$)", message = "请输入正确的身份证号")
    private String idNumber;

    /**
     * 角色ID列表。
     */
    @NotEmpty(message = "新增员工时至少选择一个角色")
    private List<Long> roleIds;
}
