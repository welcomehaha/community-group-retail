package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 员工权限视图对象。
 */
@Data
public class StaffPermissionVO implements Serializable {

    /**
     * 员工ID。
     */
    private Long employeeId;

    /**
     * 员工姓名。
     */
    private String employeeName;

    /**
     * 员工账号。
     */
    private String username;

    /**
     * 已绑定角色ID列表。
     */
    private List<Long> roleIds;

    /**
     * 已绑定角色编码列表。
     */
    private List<String> roleCodes;

    /**
     * 已绑定角色名称列表。
     */
    private List<String> roleNames;

    /**
     * 已拥有权限编码列表。
     */
    private List<String> permissionCodes;
}
