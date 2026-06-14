import request from '@/utils/request'

/**
 * 员工分页查询。
 */
export const getEmployeeList = (params: any) =>
  request({
    url: '/employee/page',
    method: 'get',
    params
  })

/**
 * 启用或禁用员工账号。
 */
export const enableOrDisableEmployee = (params: any) =>
  request({
    url: `/employee/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })

/**
 * 新增员工。
 */
export const addEmployee = (params: any) =>
  request({
    url: '/employee',
    method: 'post',
    data: params
  })

/**
 * 新增员工并分配角色。
 */
export const createEmployeeWithRoles = (params: any) =>
  request({
    url: '/employee/with-roles',
    method: 'post',
    data: params
  })

/**
 * 根据员工 ID 查询员工详情。
 */
export const getEmployeeById = (id: number) =>
  request({
    url: `/employee/${id}`,
    method: 'get'
  })

/**
 * 修改员工基础信息。
 */
export const updateEmployee = (params: any) =>
  request({
    url: '/employee',
    method: 'put',
    data: params
  })

/**
 * 查询可分配角色选项。
 */
export const getEmployeeRoleOptions = () =>
  request({
    url: '/employee/roles/options',
    method: 'get'
  })

/**
 * 查询员工权限详情。
 */
export const getEmployeePermissionInfo = (id: number) =>
  request({
    url: `/employee/${id}/permissions`,
    method: 'get'
  })

/**
 * 分配员工角色。
 */
export const assignEmployeeRoles = (id: number, data: any) =>
  request({
    url: `/employee/${id}/roles`,
    method: 'put',
    data
  })
