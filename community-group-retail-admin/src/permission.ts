import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { Message } from 'element-ui'
import { Route } from 'vue-router'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'

NProgress.configure({ 'showSpinner': false })

function hasRoutePermission(to: Route) {
  const permissionCode = to.meta && (to.meta as any).permission
  if (!permissionCode) {
    return true
  }
  const permissions = UserModule.permissions || []
  return permissions.includes(permissionCode)
}

router.beforeEach(async (to: Route, _: Route, next: any) => {
  NProgress.start()
  if (Cookies.get('token')) {
    if (UserModule.permissions.length === 0) {
      try {
        await UserModule.GetUserInfo()
      } catch (error) {
        await UserModule.ResetToken()
        Message.error('登录状态已失效，请重新登录')
        next('/login')
        return
      }
    }

    if (!hasRoutePermission(to)) {
      Message.warning('当前账号没有访问该页面的权限')
      next('/404')
      return
    }
    next()
  } else {
    if (!to.meta.notNeedAuth) {
      next('/login')
    } else {
      next()
    }
  }
})

router.afterEach((to: Route) => {
  NProgress.done()
  document.title = to.meta.title
})
