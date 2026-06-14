package com.community.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 员工角色分配请求对象。
 */
@Data
public class StaffRoleAssignDTO implements Serializable {

    /**
     * 员工ID。
     */
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    /**
     * 角色ID列表。
     */
    @NotEmpty(message = "至少选择一个角色")
    private List<Long> roleIds;
}
