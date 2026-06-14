package com.community.service.impl;

import com.community.constant.StatusConstant;
import com.community.dto.StaffCreateWithRolesDTO;
import com.community.dto.StaffLoginDTO;
import com.community.entity.Staff;
import com.community.exception.PasswordErrorException;
import com.community.mapper.StaffMapper;
import com.community.mapper.PermissionMapper;
import com.community.mapper.RoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    private StaffMapper staffMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private StaffServiceImpl staffService;

    private Staff enabledStaff;

    @BeforeEach
    void setUp() {
        enabledStaff = Staff.builder()
                .id(1L)
                .username("admin")
                .status(StatusConstant.ENABLE)
                .password(DigestUtils.md5DigestAsHex("123456".getBytes()))
                .build();
    }

    @Test
    void shouldLoginWithPlaintextPasswordAgainstHistoricalMd5Password() {
        StaffLoginDTO dto = new StaffLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        when(staffMapper.getByUsername("admin")).thenReturn(enabledStaff);

        assertDoesNotThrow(() -> staffService.login(dto));
    }

    @Test
    void shouldRejectMd5PasswordReplay() {
        StaffLoginDTO dto = new StaffLoginDTO();
        dto.setUsername("admin");
        dto.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        when(staffMapper.getByUsername("admin")).thenReturn(enabledStaff);

        assertThrows(PasswordErrorException.class, () -> staffService.login(dto));
    }

    @Test
    void shouldRejectWrongPassword() {
        StaffLoginDTO dto = new StaffLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrong-password");
        when(staffMapper.getByUsername("admin")).thenReturn(enabledStaff);

        assertThrows(PasswordErrorException.class, () -> staffService.login(dto));
    }

    @Test
    void shouldCreateStaffAndAssignRolesAtomically() {
        StaffCreateWithRolesDTO dto = new StaffCreateWithRolesDTO();
        dto.setUsername("operator01");
        dto.setName("运营专员");
        dto.setPhone("13812345678");
        dto.setSex("1");
        dto.setIdNumber("110101199001011234");
        dto.setRoleIds(List.of(11L, 12L));

        when(roleMapper.countEnabledRolesByIds(dto.getRoleIds())).thenReturn(dto.getRoleIds().size());
        // 模拟数据库自增主键回填，验证后续角色关系写入会使用新员工ID。
        doAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            staff.setId(200L);
            return null;
        }).when(staffMapper).insert(any(Staff.class));

        assertDoesNotThrow(() -> staffService.saveWithRoles(dto));

        verify(staffMapper, times(1)).insert(any(Staff.class));
        verify(roleMapper, times(1)).insertStaffRole(200L, 11L);
        verify(roleMapper, times(1)).insertStaffRole(200L, 12L);
    }

    @Test
    void shouldRejectCreateWhenRoleIdsEmpty() {
        StaffCreateWithRolesDTO dto = new StaffCreateWithRolesDTO();
        dto.setRoleIds(List.of());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> staffService.saveWithRoles(dto));

        assertEquals("新增员工时至少需要分配一个角色", exception.getMessage());
    }

    @Test
    void shouldRejectCreateWhenContainsDisabledRole() {
        StaffCreateWithRolesDTO dto = new StaffCreateWithRolesDTO();
        dto.setUsername("operator02");
        dto.setName("运营主管");
        dto.setPhone("13912345678");
        dto.setSex("2");
        dto.setIdNumber("110101199202021234");
        dto.setRoleIds(List.of(21L, 22L));

        when(roleMapper.countEnabledRolesByIds(dto.getRoleIds())).thenReturn(1);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> staffService.saveWithRoles(dto));

        assertEquals("存在无效或已禁用的角色，请刷新后重试", exception.getMessage());
        verify(staffMapper, times(0)).insert(any(Staff.class));
        verify(roleMapper, never()).insertStaffRole(anyLong(), anyLong());
    }
}
