package com.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限数据访问层。
 */
@Mapper
public interface PermissionMapper {

    /**
     * 查询员工拥有的权限编码列表。
     *
     * @param staffId 员工ID
     * @return 权限编码列表
     */
    List<String> listPermissionCodesByStaffId(@Param("staffId") Long staffId);

    /**
     * 查询员工绑定的角色数量。
     *
     * @param staffId 员工ID
     * @return 角色数量
     */
    int countRolesByStaffId(@Param("staffId") Long staffId);
}
