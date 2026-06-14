<template>
  <div class="dashboard-container home">
    <!-- 营业数据 -->
    <Overview
      v-if="canViewBusinessData"
      :overview-data="overviewData"
    />
    <!-- end -->
    <!-- 订单管理 -->
    <Orderview
      v-if="canViewOrderOverview || canViewOrderStatistics"
      :orderview-data="orderviewData"
    />
    <!-- end -->
    <div class="homeMain">
      <!-- 商品总览 -->
      <CuisineStatistics
        v-if="canViewProductOverview"
        :dishes-data="dishesData"
      />
      <!-- end -->
      <!-- 组合商品总览 -->
      <SetMealStatistics
        v-if="canViewBundleOverview"
        :set-meal-data="setMealData"
      />
      <!-- end -->
    </div>
    <!-- 订单信息 -->
    <OrderList
      v-if="canViewOrderList"
      :order-statics="orderStatics"
      @getOrderListBy3Status="getOrderListBy3Status"
    />
    <!-- end -->
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import {
  getWorkspaceBusinessData,
  getWorkspaceOrderOverview,
  getWorkspaceProductOverview,
  getWorkspaceBundleOverview,
  WorkspaceBusinessData,
  WorkspaceBundleOverview,
  WorkspaceOrderOverview,
  WorkspaceProductOverview
} from '@/api/workspace'
import { getOrderListBy, OrderListStatistics } from '@/api/order'
import { UserModule } from '@/store/modules/user'
// 组件
// 营业数据
import Overview from './components/overview.vue'
// 订单管理
import Orderview from './components/orderview.vue'
// 商品总览
import CuisineStatistics from './components/cuisineStatistics.vue'
// 组合商品总览
import SetMealStatistics from './components/setMealStatistics.vue'
// 订单列表
import OrderList from './components/orderList.vue'
@Component({
  name: 'Dashboard',
  components: {
    Overview,
    Orderview,
    CuisineStatistics,
    SetMealStatistics,
    OrderList,
  },
})
export default class extends Vue {
  private overviewData: WorkspaceBusinessData = {
    turnover: 0,
    validOrderCount: 0,
    orderCompletionRate: 0,
    unitPrice: 0,
    newUsers: 0
  }
  private orderviewData: WorkspaceOrderOverview = {
    waitingOrders: 0,
    deliveredOrders: 0,
    completedOrders: 0,
    cancelledOrders: 0,
    allOrders: 0
  }
  private flag = 2
  private tateData: string[] = []
  private dishesData: WorkspaceProductOverview = {
    sold: 0,
    discontinued: 0
  }
  private setMealData: WorkspaceBundleOverview = {
    sold: 0,
    discontinued: 0
  }
  private orderStatics: OrderListStatistics = {}
  created() {
    this.init()
  }

  /**
   * 校验当前用户是否拥有指定权限。
   */
  private hasPermission(permissionCode: string) {
    const permissions = UserModule.permissions || []
    return permissions.includes(permissionCode)
  }

  get canViewBusinessData() {
    return this.hasPermission('dashboard:workspace:business-data')
  }

  get canViewOrderOverview() {
    return this.hasPermission('dashboard:workspace:order-overview')
  }

  get canViewProductOverview() {
    return this.hasPermission('dashboard:workspace:product-overview')
  }

  get canViewBundleOverview() {
    return this.hasPermission('dashboard:workspace:bundle-overview')
  }

  get canViewOrderStatistics() {
    return this.hasPermission('order:manage:statistics')
  }

  get canViewOrderList() {
    return this.hasPermission('order:manage:list')
  }

  init() {
    this.$nextTick(() => {
      if (this.canViewBusinessData) {
        this.getBusinessData()
      }
      if (this.canViewOrderOverview) {
        this.getOrderStatisticsData()
      }
      if (this.canViewProductOverview) {
        this.getOverStatisticsData()
      }
      if (this.canViewBundleOverview) {
        this.getSetMealStatisticsData()
      }
    })
  }
  // 获取营业数据
  async getBusinessData() {
    const data = await getWorkspaceBusinessData()
    this.overviewData = data.data.data
  }
  // 获取今日订单
  async getOrderStatisticsData() {
    const data = await getWorkspaceOrderOverview()
    this.orderviewData = data.data.data
  }
  // 获取商品总览数据
  async getOverStatisticsData() {
    const data = await getWorkspaceProductOverview()
    this.dishesData = data.data.data
  }
  // 获取组合商品总览数据
  async getSetMealStatisticsData() {
    const data = await getWorkspaceBundleOverview()
    this.setMealData = data.data.data
  }
  //获取待处理，待履约配送，履约中数量
  getOrderListBy3Status() {
    if (!this.canViewOrderStatistics) {
      return
    }
    getOrderListBy()
      .then((res) => {
        if (res.data.code === 1) {
          this.orderStatics = res.data.data
        } else {
          this.$message.error(res.data.msg)
        }
      })
      .catch((err) => {
        this.$message.error('请求出错了：' + err.message)
      })
  }
}
</script>

<style lang="scss">
</style>
