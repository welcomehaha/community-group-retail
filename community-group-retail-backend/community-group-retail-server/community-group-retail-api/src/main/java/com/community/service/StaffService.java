package com.community.service;

import com.community.dto.StaffDTO;
import com.community.dto.StaffCreateWithRolesDTO;
import com.community.dto.StaffLoginDTO;
import com.community.dto.StaffPageQueryDTO;
import com.community.dto.StaffRoleAssignDTO;
import com.community.entity.Staff;
import com.community.result.PageResult;
import com.community.vo.StaffPermissionVO;
import com.community.vo.RoleOptionVO;

import java.util.List;

public interface StaffService {

    /**
     * 员工登录
     * @param staffLoginDTO
     * @return
     */
    Staff login(StaffLoginDTO staffLoginDTO);

    /**
     * 新增员工
     * @param staffDTO
     */
    void save(StaffDTO staffDTO);

    /**
     * 新增员工并分配角色。
     *
     * @param staffCreateWithRolesDTO 新增员工与角色请求
     */
    void saveWithRoles(StaffCreateWithRolesDTO staffCreateWithRolesDTO);

    /**
     * 分页查询
     * @param staffPageQueryDTO
     * @return
     */
    PageResult pageQuery(StaffPageQueryDTO staffPageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Staff getById(Long id);

    /**
     * 编辑员工信息
     * @param staffDTO
     */
    void update(StaffDTO staffDTO);

    /**
     * 查询可分配角色选项。
     *
     * @return 角色选项列表
     */
    List<RoleOptionVO> listRoleOptions();

    /**
     * 查询员工授权详情。
     *
     * @param staffId 员工ID
     * @return 授权详情
     */
    StaffPermissionVO getStaffPermissionInfo(Long staffId);

    /**
     * 分配员工角色。
     *
     * @param assignDTO 分配请求
     */
    void assignRoles(StaffRoleAssignDTO assignDTO);
}
