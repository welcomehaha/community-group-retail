import Vue from 'vue'
import { UserModule } from '@/store/modules/user'

/**
 * 按权限编码控制按钮显隐。
 */
Vue.directive('permission', {
  inserted(el, binding) {
    const permissionCode = binding.value
    const permissions = UserModule.permissions || []

    if (!permissionCode) {
      el.parentNode && el.parentNode.removeChild(el)
      return
    }

    if (!permissions.includes(permissionCode)) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
})
