<template>
  <div>
    <div class="logo">
      <!-- <img
        src="./../../../assets/logo.png"
        width="122.5"
        alt=""
      > -->
      <!-- <img
        src="@/assets/login/login-logo.png"
        alt=""
        style="width: 120px; height: 31px"
      /> -->
      <div v-if="!isCollapse"
           class="sidebar-logo"
      >
        <img src="@/assets/login/logo.png"
             style="width: 108px; height: 108px"
        >
        <div class="brand-copy">
          <div class="brand-title">
            社区团购即时零售平台
          </div>
          <div class="brand-subtitle">
            门店运营管理后台
          </div>
        </div>
      </div>
      <div v-else
           class="sidebar-logo-mini"
           title="社区团购即时零售平台"
      >
        <img src="@/assets/login/mini-logo.png">
        <span class="mini-title">社区零售</span>
      </div>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu :default-openeds="defOpen"
               :default-active="defAct"
               :collapse="isCollapse"
               :background-color="variables.menuBg"
               :text-color="variables.menuText"
               :active-text-color="variables.menuActiveText"
               :unique-opened="false"
               :collapse-transition="false"
               mode="vertical"
      >
        <sidebar-item v-for="route in routes"
                      :key="route.path"
                      :item="route"
                      :base-path="route.path"
                      :is-collapse="isCollapse"
        />
        <!-- <div class="sub-menu">
          <div class="avatarName">
            {{ name }}
          </div>
          <div class="img">
            <img
              src="./../../../assets/icons/btn_close@2x.png"
              class="outLogin"
              alt="退出"
              @click="logout"
            />
          </div>
        </div> -->
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'
import { AppModule } from '@/store/modules/app'
import { UserModule } from '@/store/modules/user'
import SidebarItem from './SidebarItem.vue'
import variables from '@/styles/_variables.scss'
import { getSidebarStatus, setSidebarStatus } from '@/utils/cookies'
import Cookies from 'js-cookie'
@Component({
  name: 'SideBar',
  components: {
    SidebarItem
  }
})
export default class extends Vue {
  private restKey: number = 0
  get name() {
    return (UserModule.userInfo as any).name
      ? (UserModule.userInfo as any).name
      : JSON.parse(Cookies.get('user_info') as any).name
  }
  get defOpen() {
    // const urlArr = this.$route.path.split('/')
    // const openStr = urlArr.length > 2 ? `/${urlArr[1]}` : '/'
    let path = ['/']
    this.routes.forEach((n: any, i: number) => {
      if (n.meta.roles && n.meta.roles[0] === this.roles[0]) {
        path.splice(0, 1, n.path)
      }
    })
    return path
  }

  get defAct() {
    let path = this.$route.path
    return path
  }

  get sidebar() {
    return AppModule.sidebar
  }

  get roles() {
    return UserModule.roles
  }

  get routes() {
    const routes = JSON.parse(
      JSON.stringify([...(this.$router as any).options.routes])
    )
    let menuList = []
    const menu = routes.find(item => item.path === '/')
    if (menu) {
      menuList = this.filterRoutesByPermission(menu.children || [])
    }
    return menuList
  }

  get variables() {
    return variables
  }

  get isCollapse() {
    return !this.sidebar.opened
  }
  private async logout() {
    this.$store.dispatch('LogOut').then(() => {
      // location.href = '/'
      this.$router.replace({ path: '/login' })
    })
    // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
  }

  /**
   * 递归过滤侧边栏菜单，只保留当前账号具备权限的路由。
   */
  private filterRoutesByPermission(routeList: any[]) {
    return routeList
      .filter((route: any) => this.hasRoutePermission(route))
      .map((route: any) => {
        const currentRoute = { ...route }
        if (currentRoute.children && currentRoute.children.length > 0) {
          currentRoute.children = this.filterRoutesByPermission(currentRoute.children)
        }
        return currentRoute
      })
  }

  /**
   * 判断当前路由是否有访问权限，未配置权限编码的菜单默认放行。
   */
  private hasRoutePermission(route: any) {
    const permissionCode = route && route.meta ? route.meta.permission : ''
    if (!permissionCode) {
      return true
    }
    return (UserModule.permissions || []).includes(permissionCode)
  }
}
</script>

<style lang="scss" scoped>
.logo {
  text-align: center;
  background: linear-gradient(180deg, #0b2f2a 0%, #0f3f37 100%);
  padding: 14px 14px 0;
  height: 172px;
  img {
    display: inline-block;
  }
}
.sidebar-logo {
  height: 142px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.brand-copy {
  margin-top: 8px;
  color: #fff;
  text-align: center;
}
.brand-title {
  font-size: 18px;
  line-height: 26px;
  font-weight: 600;
}
.brand-subtitle {
  margin-top: 2px;
  font-size: 12px;
  line-height: 18px;
  color: rgba(255, 255, 255, 0.72);
}
.sidebar-logo-mini {
  height: 142px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  img {
    width: 72px;
    height: 72px;
  }
}
.mini-title {
  margin-top: 6px;
  font-size: 12px;
  line-height: 18px;
  color: rgba(255, 255, 255, 0.88);
}
.el-scrollbar {
  height: 100%;
  background-color: #0b2f2a;
}

.el-menu {
  border: none;
  height: calc(95vh - 135px);
  width: 100% !important;
  padding: 24px 15px 0;
}
</style>
