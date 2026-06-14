/**
 * 员工模块前端校验规则。
 */
const USERNAME_PATTERN = /^[A-Za-z0-9_]+$/
const NAME_PATTERN = /^[A-Za-z\u4e00-\u9fa5]+$/
const PHONE_PATTERN = /^1[3-9]\d{9}$/
const ID_NUMBER_PATTERN = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(X|x)$)/

/**
 * 校验员工账号。
 */
export const validateEmployeeUsername = (rule: any, value: string, callback: Function) => {
  const trimmedValue = (value || '').trim()
  if (!trimmedValue) {
    callback(new Error('请输入员工账号'))
    return
  }
  if (trimmedValue.length < 4 || trimmedValue.length > 20) {
    callback(new Error('员工账号长度应为4-20位'))
    return
  }
  if (!USERNAME_PATTERN.test(trimmedValue)) {
    callback(new Error('员工账号只能包含字母、数字和下划线'))
    return
  }
  callback()
}

/**
 * 校验员工姓名。
 */
export const validateEmployeeName = (rule: any, value: string, callback: Function) => {
  const trimmedValue = (value || '').trim()
  if (!trimmedValue) {
    callback(new Error('请输入员工姓名'))
    return
  }
  if (trimmedValue.length < 2 || trimmedValue.length > 20) {
    callback(new Error('员工姓名长度应为2-20位'))
    return
  }
  if (!NAME_PATTERN.test(trimmedValue)) {
    callback(new Error('员工姓名只能包含中文或英文字母'))
    return
  }
  callback()
}

/**
 * 校验手机号。
 */
export const validateEmployeePhone = (rule: any, value: string, callback: Function) => {
  const trimmedValue = (value || '').trim()
  if (!trimmedValue) {
    callback(new Error('请输入正确的手机号'))
    return
  }
  if (!PHONE_PATTERN.test(trimmedValue)) {
    callback(new Error('请输入正确的手机号'))
    return
  }
  callback()
}

/**
 * 校验身份证号。
 */
export const validateEmployeeIdNumber = (rule: any, value: string, callback: Function) => {
  const trimmedValue = (value || '').trim()
  if (!trimmedValue) {
    callback(new Error('请输入正确的身份证号'))
    return
  }
  if (!ID_NUMBER_PATTERN.test(trimmedValue)) {
    callback(new Error('请输入正确的身份证号'))
    return
  }
  callback()
}

/**
 * 校验角色列表。
 */
export const validateEmployeeRoleIds = (rule: any, value: number[], callback: Function) => {
  if (!value || value.length === 0) {
    callback(new Error('请至少选择一个员工角色'))
    return
  }
  callback()
}
