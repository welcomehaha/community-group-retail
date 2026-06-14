<template>
  <div class="dashboard-container home">
    <!-- 标题 -->
    <TitleIndex :flag="flag" :tate-data="tateData" @sendTitleInd="getTitleNum" />
    <!-- end -->
    <div class="homeMain">
      <!-- GMV统计 -->
      <TurnoverStatistics
        v-if="canViewTurnover"
        :turnoverdata="turnoverData"
      />
      <!-- end -->
      <!-- 用户统计 -->
      <UserStatistics
        v-if="canViewUserStatistics"
        :userdata="userData"
      />
      <!-- end -->
    </div>
    <div class="homeMain homecon">
      <!-- 订单统计 -->
      <OrderStatistics
        v-if="canViewOrderStatistics"
        :orderdata="orderData"
        :overview-data="overviewData"
      />
      <!-- end -->
      <!-- 销量排名TOP10 -->
      <Top
        v-if="canViewTop10"
        :top10data="top10Data"
      />
      <!-- end -->
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import {
  get1stAndToday,
  past7Day,
  past30Day,
  pastWeek,
  pastMonth,
} from '@/utils/formValidate'
import {
  getReportTurnoverStatistics,
  getReportUserStatistics,
  getReportOrderStatistics,
  getReportTop10,
  ReportOrderStatistics,
  ReportTop10Statistics,
  ReportTurnoverStatistics,
  ReportUserStatistics,
} from '@/api/report'
import { UserModule } from '@/store/modules/user'

interface StatisticsOverviewData {
  turnover: number
  turnoverGrowth?: number
  validOrderCount: number
  validOrderCountGrowth?: number
  orderCompletionRate: number
  orderCompletionRateGrowth?: number
  unitPrice: number
  unitPriceGrowth?: number
  totalUsers: number
  newUsers: number
  newUsersGrowth?: number
}
// 组件
// 标题
import TitleIndex from './components/titleIndex.vue'
// GMV统计
import TurnoverStatistics from './components/turnoverStatistics.vue'
// 用户统计
import UserStatistics from './components/userStatistics.vue'
// 订单统计
import OrderStatistics from './components/orderStatistics.vue'
// 排名
import Top from './components/top10.vue'
@Component({
  name: 'Dashboard',
  components: {
    TitleIndex,
    TurnoverStatistics,
    UserStatistics,
    OrderStatistics,
    Top,
  },
})
export default class extends Vue {
  private overviewData: StatisticsOverviewData = {
    turnover: 0,
    validOrderCount: 0,
    orderCompletionRate: 0,
    unitPrice: 0,
    totalUsers: 0,
    newUsers: 0
  }
  private flag = 2
  private tateData: string[] = []
  private turnoverData: ReportTurnoverStatistics = {
    dateList: [],
    turnoverList: []
  }
  private userData: ReportUserStatistics = {
    dateList: [],
    totalUserList: [],
    newUserList: []
  }
  private orderData: ReportOrderStatistics = {
    dateList: [],
    orderCountList: [],
    validOrderCountList: [],
    totalOrderCount: 0,
    validOrderCount: 0,
    orderCompletionRate: 0
  }
  private top10Data: ReportTop10Statistics = {
    nameList: [],
    numberList: []
  }
  created() {
    //this.init(this.flag)
    this.getTitleNum(2);
  }

  /**
   * 校验当前用户是否拥有指定权限。
   */
  private hasPermission(permissionCode: string) {
    const permissions = UserModule.permissions || []
    return permissions.includes(permissionCode)
  }

  get canViewTurnover() {
    return this.hasPermission('report:statistics:turnover')
  }

  get canViewUserStatistics() {
    return this.hasPermission('report:statistics:user')
  }

  get canViewOrderStatistics() {
    return this.hasPermission('report:statistics:order')
  }

  get canViewTop10() {
    return this.hasPermission('report:statistics:top10')
  }

  // 获取基本数据
  init(begin: any,end:any) {
    this.$nextTick(() => {
      if (this.canViewTurnover) {
        this.getTurnoverStatisticsData(begin,end)
      }
      if (this.canViewUserStatistics) {
        this.getUserStatisticsData(begin,end)
      }
      if (this.canViewOrderStatistics) {
        this.getOrderStatisticsData(begin,end)
      }
      if (this.canViewTop10) {
        this.getTopData(begin,end)
      }
    })
  }

  // 获取GMV统计数据
  async getTurnoverStatisticsData(begin: any ,end:any) {
    this.turnoverData = await getReportTurnoverStatistics({ begin: begin,end:end })
  }
  // 获取用户统计数据
  async getUserStatisticsData(begin: any ,end:any) {
    this.userData = await getReportUserStatistics({ begin: begin,end:end })
  }
  // 获取订单统计数据
  async getOrderStatisticsData(begin: any ,end:any) {
    this.orderData = await getReportOrderStatistics({begin: begin,end:end })
  }
  // 获取排行数据
  async getTopData(begin: any ,end:any) {
    this.top10Data = await getReportTop10({begin: begin,end:end })
  }
  // 获取当前选中的tab时间
  getTitleNum(data) {
    switch (data) {
      case 1:
        this.tateData = get1stAndToday()
        break
      case 2:
        this.tateData = past7Day()
        break
      case 3:
        this.tateData = past30Day()
        break
      case 4:
        this.tateData = pastWeek()
        break
      case 5:
        this.tateData = pastMonth()
        break
    }
    this.init(this.tateData[0],this.tateData[1])
  }
}
</script>

<style lang="scss">
</style>
