package com.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体。
 */
@Data
public class Permission implements Serializable {

    private Long id;

    /**
     * 权限编码。
     */
    private String permissionCode;

    /**
     * 权限名称。
     */
    private String permissionName;

    /**
     * 权限类型：MENU / BUTTON / API。
     */
    private String permissionType;

    /**
     * 所属业务域。
     */
    private String moduleCode;

    /**
     * 状态：1启用 0禁用。
     */
    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
