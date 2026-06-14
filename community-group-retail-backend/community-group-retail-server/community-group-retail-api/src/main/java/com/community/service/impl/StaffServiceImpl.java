package com.community.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.constant.MessageConstant;
import com.community.constant.PasswordConstant;
import com.community.constant.StatusConstant;
import com.community.context.BaseContext;
import com.community.dto.StaffDTO;
import com.community.dto.StaffCreateWithRolesDTO;
import com.community.dto.StaffLoginDTO;
import com.community.dto.StaffPageQueryDTO;
import com.community.dto.StaffRoleAssignDTO;
import com.community.entity.Staff;
import com.community.exception.AccountLockedException;
import com.community.exception.AccountNotFoundException;
import com.community.exception.PasswordErrorException;
import com.community.mapper.StaffMapper;
import com.community.mapper.PermissionMapper;
import com.community.mapper.RoleMapper;
import com.community.result.PageResult;
import com.community.service.StaffService;
import com.community.vo.StaffPermissionVO;
import com.community.vo.RoleOptionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 员工登录
     *
     * @param staffLoginDTO
     * @return
     */
    public Staff login(StaffLoginDTO staffLoginDTO) {
        String username = staffLoginDTO.getUsername();
        String password = staffLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Staff staff = staffMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (staff == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 登录口令必须是明文密码，由服务端统一做 MD5 校验
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!staff.getPassword().equals(encryptedPassword)) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (staff.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return staff;
    }

    /**
     * 新增员工
     *
     * @param staffDTO
     */
    public void save(StaffDTO staffDTO) {
        Staff staff = buildStaffForCreate(staffDTO.getName(),
                staffDTO.getUsername(),
                staffDTO.getPhone(),
                staffDTO.getSex(),
                staffDTO.getIdNumber());
        staffMapper.insert(staff);
    }

    /**
     * 新增员工并分配角色。
     *
     * @param staffCreateWithRolesDTO 新增员工与角色请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithRoles(StaffCreateWithRolesDTO staffCreateWithRolesDTO) {
        if (CollectionUtils.isEmpty(staffCreateWithRolesDTO.getRoleIds())) {
            throw new IllegalArgumentException("新增员工时至少需要分配一个角色");
        }

        int enabledRoleCount = roleMapper.countEnabledRolesByIds(staffCreateWithRolesDTO.getRoleIds());
        if (enabledRoleCount != staffCreateWithRolesDTO.getRoleIds().size()) {
            throw new IllegalArgumentException("存在无效或已禁用的角色，请刷新后重试");
        }

        Staff staff = buildStaffForCreate(staffCreateWithRolesDTO.getName(),
                staffCreateWithRolesDTO.getUsername(),
                staffCreateWithRolesDTO.getPhone(),
                staffCreateWithRolesDTO.getSex(),
                staffCreateWithRolesDTO.getIdNumber());
        staffMapper.insert(staff);

        // 员工创建成功后立即绑定角色，避免出现“新员工无角色”的窗口期。
        for (Long roleId : staffCreateWithRolesDTO.getRoleIds()) {
            roleMapper.insertStaffRole(staff.getId(), roleId);
        }
    }

    /**
     * 构建新增员工实体。
     *
     * @param name 员工姓名
     * @param username 员工账号
     * @param phone 手机号
     * @param sex 性别
     * @param idNumber 身份证号
     * @return 员工实体
     */
    private Staff buildStaffForCreate(String name, String username, String phone, String sex, String idNumber) {
        Staff staff = new Staff();

        staff.setName(name);
        staff.setUsername(username);
        staff.setPhone(phone);
        staff.setSex(sex);
        staff.setIdNumber(idNumber);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        staff.setStatus(StatusConstant.ENABLE);

        //设置密码，默认密码123456
        staff.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        //staff.setCreateTime(LocalDateTime.now());
        //staff.setUpdateTime(LocalDateTime.now());

        //设置当前记录创建人id和修改人id
        //staff.setCreateUser(BaseContext.getCurrentId());
        //staff.setUpdateUser(BaseContext.getCurrentId());
        return staff;
    }

    /**
     * 分页查询
     *
     * @param staffPageQueryDTO
     * @return
     */
    public PageResult pageQuery(StaffPageQueryDTO staffPageQueryDTO) {
        // select * from staff limit 0,10
        //开始分页查询
        PageHelper.startPage(staffPageQueryDTO.getPage(), staffPageQueryDTO.getPageSize());

        Page<Staff> page = staffMapper.pageQuery(staffPageQueryDTO);

        long total = page.getTotal();
        List<Staff> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        // update staff set status = ? where id = ?

        /*Staff staff = new Staff();
        staff.setStatus(status);
        staff.setId(id);*/

        Staff staff = Staff.builder()
                .status(status)
                .id(id)
                .build();

        staffMapper.update(staff);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    public Staff getById(Long id) {
        Staff staff = staffMapper.getById(id);
        staff.setPassword("****");
        return staff;
    }

    /**
     * 编辑员工信息
     *
     * @param staffDTO
     */
    public void update(StaffDTO staffDTO) {
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffDTO, staff);

        //staff.setUpdateTime(LocalDateTime.now());
        //staff.setUpdateUser(BaseContext.getCurrentId());

        staffMapper.update(staff);
    }

    /**
     * 查询可分配角色选项。
     *
     * @return 角色列表
     */
    @Override
    public List<RoleOptionVO> listRoleOptions() {
        return roleMapper.listEnabledRoles();
    }

    /**
     * 查询员工授权详情。
     *
     * @param staffId 员工ID
     * @return 授权详情
     */
    @Override
    public StaffPermissionVO getStaffPermissionInfo(Long staffId) {
        Staff staff = staffMapper.getById(staffId);
        if (staff == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        StaffPermissionVO permissionVO = new StaffPermissionVO();
        permissionVO.setEmployeeId(staff.getId());
        permissionVO.setEmployeeName(staff.getName());
        permissionVO.setUsername(staff.getUsername());
        permissionVO.setRoleIds(roleMapper.listRoleIdsByStaffId(staffId));
        permissionVO.setRoleCodes(roleMapper.listRoleCodesByStaffId(staffId));
        permissionVO.setRoleNames(roleMapper.listRoleNamesByStaffId(staffId));
        permissionVO.setPermissionCodes(permissionMapper.listPermissionCodesByStaffId(staffId));
        return permissionVO;
    }

    /**
     * 分配员工角色。
     *
     * @param assignDTO 分配请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(StaffRoleAssignDTO assignDTO) {
        Staff staff = staffMapper.getById(assignDTO.getEmployeeId());
        if (staff == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 服务层兜底校验，避免绕过控制器校验后把员工角色清空为“无角色”状态。
        if (CollectionUtils.isEmpty(assignDTO.getRoleIds())) {
            throw new IllegalArgumentException("员工至少需要分配一个角色");
        }

        int enabledRoleCount = roleMapper.countEnabledRolesByIds(assignDTO.getRoleIds());
        if (enabledRoleCount != assignDTO.getRoleIds().size()) {
            throw new IllegalArgumentException("存在无效或已禁用的角色，请刷新后重试");
        }

        roleMapper.deleteRolesByStaffId(assignDTO.getEmployeeId());
        for (Long roleId : assignDTO.getRoleIds()) {
            roleMapper.insertStaffRole(assignDTO.getEmployeeId(), roleId);
        }
    }
}
