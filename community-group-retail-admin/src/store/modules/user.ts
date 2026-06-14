import { VuexModule, Module, Action, Mutation, getModule } from 'vuex-module-decorators'
import { login, logout } from '@/api/auth'
import { getEmployeePermissionInfo } from '@/api/employee'
import { getToken, setToken, removeToken,getStoreId, setStoreId, removeStoreId, setUserInfo, getUserInfo, removeUserInfo, getPermissionCodes, setPermissionCodes, removePermissionCodes } from '@/utils/cookies'
import store from '@/store'
import Cookies from 'js-cookie'
import { Message } from 'element-ui'
export interface IUserState {
  token: string
  name: string
  avatar: string
  storeId: string
  introduction: string
  userInfo: any
  roles: string[]
  permissions: string[]
  username: string
}

@Module({ 'dynamic': true, store, 'name': 'user' })
class User extends VuexModule implements IUserState {
  public token = getToken() || ''
  public name = ''
  public avatar = ''
  // @ts-ignore
  public storeId: string = getStoreId() || ''
  public introduction = ''
  public userInfo = {}
  public roles: string[] = []
  public permissions: string[] = JSON.parse(getPermissionCodes() || '[]')
  public username = Cookies.get('username') || ''

  @Mutation
  private SET_TOKEN(token: string) {
    this.token = token
  }

  @Mutation
  private SET_NAME(name: string) {
    this.name = name
  }

  @Mutation
  private SET_USERINFO(userInfo: any) {
    this.userInfo = { ...userInfo }
  }

  @Mutation
  private SET_AVATAR(avatar: string) {
    this.avatar = avatar
  }

  @Mutation
  private SET_INTRODUCTION(introduction: string) {
    this.introduction = introduction
  }

  @Mutation
  private SET_ROLES(roles: string[]) {
    this.roles = roles
  }

  @Mutation
  private SET_PERMISSIONS(permissions: string[]) {
    this.permissions = permissions
  }

  @Mutation
  private SET_STOREID(storeId: string) {
    this.storeId = storeId
  }
  @Mutation
  private SET_USERNAME(name: string) {
    this.username = name
    }

  @Action
  public async Login(userInfo: { username: string, password: string }) {
    let { username, password } = userInfo
    username = username.trim()
    // 登录成功后立刻补拉权限信息，保证按钮权限和路由权限口径一致。
    const { data } = await login({ username, password })
    if (String(data.code) === '1') {
      const permissionResp = await getEmployeePermissionInfo(data.data.id)
      const permissionData = permissionResp.data && permissionResp.data.data ? permissionResp.data.data : {}
      const permissionCodes = permissionData.permissionCodes || []
      const roleCodes = permissionData.roleCodes || []
      const mergedUserInfo = {
        ...data.data,
        roles: roleCodes,
        permissionCodes
      }
      //设置vuex中属性的值
      this.SET_USERNAME(username)
      this.SET_TOKEN(data.data.token)
      this.SET_USERINFO(mergedUserInfo)
      this.SET_ROLES(roleCodes)
      this.SET_PERMISSIONS(permissionCodes)

      //保存到Cookie中
      Cookies.set('username', username)
      Cookies.set('user_info', JSON.stringify(mergedUserInfo))
      Cookies.set('token', data.data.token);
      setUserInfo(mergedUserInfo)
      setPermissionCodes(permissionCodes)
      return data
    } else {
      return Message.error(data.msg)
    }
  }

  @Action
  public ResetToken () {
    removeToken()
    removePermissionCodes()
    this.SET_TOKEN('')
    this.SET_ROLES([])
    this.SET_PERMISSIONS([])
  }

  @Action
  public async changeStore(data: any) {
    this.SET_STOREID = data.data
    this.SET_TOKEN(data.authorization)
    setStoreId(data.data)
    setToken(data.authorization)
  }

  @Action
  public async GetUserInfo () {
    if (this.token === '') {
      throw Error('GetUserInfo: token is undefined!')
    }

    const rawUserInfo = getUserInfo()
    const data = rawUserInfo ? JSON.parse(rawUserInfo) : null //  { roles: ['admin'], name: 'zhangsan', avatar: '/login', introduction: '' }
    if (!data) {
      throw Error('Verification failed, please Login again.')
    }

    const { roles, permissionCodes = [], name, avatar, introduction, applicant, storeManagerName, storeId='' } = data // data.user
    // roles must be a non-empty array
    if (!roles || roles.length <= 0) {
      throw Error('GetUserInfo: roles must be a non-null array!')
    }

    this.SET_ROLES(roles)
    this.SET_PERMISSIONS(permissionCodes)
    this.SET_USERINFO(data)
    this.SET_NAME(name || applicant || storeManagerName)
    this.SET_AVATAR(avatar)
    this.SET_INTRODUCTION(introduction)
  }

  @Action
  public async LogOut () {
    await logout()
    removeToken()
    removePermissionCodes()
    this.SET_TOKEN('')
    this.SET_ROLES([])
    this.SET_PERMISSIONS([])
    Cookies.remove('username')
    Cookies.remove('user_info')
    removeUserInfo()
  }
}

export const UserModule = getModule(User)
