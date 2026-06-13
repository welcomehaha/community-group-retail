package com.community.mapper;

import com.community.vo.RoleOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问层。
 */
@Mapper
public interface RoleMapper {

    /**
     * 查询所有启用角色。
     *
     * @return 角色列表
     */
    List<RoleOptionVO> listEnabledRoles();

    /**
     * 查询员工已分配的角色ID列表。
     *
     * @param staffId 员工ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByStaffId(@Param("staffId") Long staffId);

    /**
     * 查询员工已分配的角色编码列表。
     *
     * @param staffId 员工ID
     * @return 角色编码列表
     */
    List<String> listRoleCodesByStaffId(@Param("staffId") Long staffId);

    /**
     * 查询员工已分配的角色名称列表。
     *
     * @param staffId 员工ID
     * @return 角色名称列表
     */
    List<String> listRoleNamesByStaffId(@Param("staffId") Long staffId);

    /**
     * 删除员工已有角色关系。
     *
     * @param staffId 员工ID
     */
    void deleteRolesByStaffId(@Param("staffId") Long staffId);

    /**
     * 新增员工角色关系。
     *
     * @param staffId 员工ID
     * @param roleId 角色ID
     */
    void insertStaffRole(@Param("staffId") Long staffId, @Param("roleId") Long roleId);

    /**
     * 统计启用状态下的角色数量。
     *
     * @param roleIds 角色ID列表
     * @return 角色数量
     */
    int countEnabledRolesByIds(@Param("roleIds") List<Long> roleIds);
}
