package com.community.controller.admin;

import com.community.constant.JwtClaimsConstant;
import com.community.dto.StaffDTO;
import com.community.dto.StaffCreateWithRolesDTO;
import com.community.dto.StaffLoginDTO;
import com.community.dto.StaffPageQueryDTO;
import com.community.dto.StaffRoleAssignDTO;
import com.community.entity.Staff;
import com.community.properties.JwtProperties;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.StaffService;
import com.community.utils.JwtUtil;
import com.community.vo.StaffPermissionVO;
import com.community.vo.StaffLoginVO;
import com.community.vo.RoleOptionVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Validated
@Tag(name = "员工相关接口")
public class EmployeeController {

    @Autowired
    private StaffService staffService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param staffLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<StaffLoginVO> login(@RequestBody StaffLoginDTO staffLoginDTO) {
        log.info("员工登录：{}", staffLoginDTO);

        Staff staff = staffService.login(staffLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, staff.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        StaffLoginVO staffLoginVO = StaffLoginVO.builder()
                .id(staff.getId())
                .userName(staff.getUsername())
                .name(staff.getName())
                .token(token)
                .build();

        return Result.success(staffLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param staffDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增员工")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:add')")
    public Result save(@Valid @RequestBody StaffDTO staffDTO){
        log.info("新增员工：{}",staffDTO);
        staffService.save(staffDTO);
        return Result.success();
    }

    /**
     * 新增员工并分配角色。
     *
     * @param staffCreateWithRolesDTO 员工与角色信息
     * @return 处理结果
     */
    @PostMapping("/with-roles")
    @Operation(summary = "新增员工并分配角色")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:add')")
    public Result saveWithRoles(@Valid @RequestBody StaffCreateWithRolesDTO staffCreateWithRolesDTO){
        log.info("新增员工并分配角色：{}", staffCreateWithRolesDTO);
        staffService.saveWithRoles(staffCreateWithRolesDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param staffPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:list')")
    public Result<PageResult> page(StaffPageQueryDTO staffPageQueryDTO){
        log.info("员工分页查询，参数为：{}", staffPageQueryDTO);
        PageResult pageResult = staffService.pageQuery(staffPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用员工账号")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:status')")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        staffService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工信息")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:detail')")
    public Result<Staff> getById(@PathVariable Long id){
        Staff staff = staffService.getById(id);
        return Result.success(staff);
    }

    /**
     * 编辑员工信息
     * @param staffDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑员工信息")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:edit')")
    public Result update(@Valid @RequestBody StaffDTO staffDTO){
        log.info("编辑员工信息：{}", staffDTO);
        staffService.update(staffDTO);
        return Result.success();
    }

    /**
     * 查询角色选项列表。
     *
     * @return 角色选项列表
     */
    @GetMapping("/roles/options")
    @Operation(summary = "查询员工角色选项")
    @PreAuthorize("@permissionService.hasAuthority('employee:role:list')")
    public Result<List<RoleOptionVO>> roleOptions() {
        return Result.success(staffService.listRoleOptions());
    }

    /**
     * 查询员工授权详情。
     *
     * @param id 员工ID
     * @return 授权详情
     */
    @GetMapping("/{id}/permissions")
    @Operation(summary = "查询员工权限详情")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:authorize-view')")
    public Result<StaffPermissionVO> permissionInfo(@PathVariable Long id) {
        return Result.success(staffService.getStaffPermissionInfo(id));
    }

    /**
     * 分配员工角色。
     *
     * @param id 员工ID
     * @param assignDTO 分配请求
     * @return 处理结果
     */
    @PutMapping("/{id}/roles")
    @Operation(summary = "分配员工角色")
    @PreAuthorize("@permissionService.hasAuthority('employee:user:assign-role')")
    public Result assignRoles(@PathVariable Long id, @Valid @RequestBody StaffRoleAssignDTO assignDTO) {
        assignDTO.setEmployeeId(id);
        staffService.assignRoles(assignDTO);
        return Result.success();
    }
}


