import request from '@/utils/request'

/**
 * 管理端员工登录。
 */
export const login = (data: any) => {
  return request({
    url: '/employee/login',
    method: 'post',
    data
  })
}

/**
 * 管理端员工退出登录。
 */
export const logout = () => {
  return request({
    url: '/employee/logout',
    method: 'post'
  })
}

/**
 * 修改当前登录用户密码。
 */
export const updatePassword = (data: any) => {
  return request({
    url: '/employee/editPassword',
    method: 'put',
    data
  })
}
