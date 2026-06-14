package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色下拉选项视图对象。
 */
@Data
public class RoleOptionVO implements Serializable {

    /**
     * 角色ID。
     */
    private Long id;

    /**
     * 角色编码。
     */
    private String roleCode;

    /**
     * 角色名称。
     */
    private String roleName;
}
