<template>
  <div class="container">
    <h2 class="homeTitle">
      用户统计
    </h2>
    <div class="charBox">
      <div id="usermain" style="width: 100%; height: 320px" />
      <ul class="orderListLine user">
        <li class="one">
          <span />用户总量（个）
        </li>
        <li class="three">
          <span />新增用户（个）
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import * as echarts from 'echarts'
import { ReportUserStatistics } from '@/api/report'
@Component({
  name: 'UserStatistics',
})
export default class extends Vue {
  @Prop() private userdata!: ReportUserStatistics
  @Watch('userdata')
  getData() {
    this.$nextTick(() => {
      this.initChart()
    })
  }
  initChart() {
    const chartDom = document.getElementById('usermain')
    if (!chartDom) {
      return
    }
    const myChart = echarts.init(chartDom)
    const option: any = {
      // legend: {
      //   itemHeight: 3, //图例高
      //   itemWidth: 12, //图例宽
      //   icon: 'rect', //图例
      //   show: true,
      //   top: 'bottom',
      //   data: ['用户总量', '新增用户'],
      // },
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#fff', //背景颜色（此时为默认色）
        borderRadius: 2, //边框圆角
        textStyle: {
          color: '#333', //字体颜色
          fontSize: 12, //字体大小
          fontWeight: 300,
        },
      },
      grid: {
        top: '5%',
        left: '20',
        right: '50',
        bottom: '12%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        axisLabel: {
          //X轴字体颜色
          textStyle: {
            color: '#666',
            fontSize: '12px',
          },
        },
        axisLine: {
          //X轴线颜色
          lineStyle: {
            color: '#E5E4E4',
            width: 1, //x轴线的宽度
          },
        },
        data: this.userdata.dateList, //后端传来的动态数据
      },
      yAxis: [
        {
          type: 'value',
          min: 0,
          //max: 500,
          //interval: 100,
          axisLabel: {
            textStyle: {
              color: '#666',
              fontSize: '12px',
            },
            // formatter: "{value} ml",//单位
          },
        }, //左侧值
      ],
      series: [
        {
          name: '用户总量',
          type: 'line',
          // stack: 'Total',
          smooth: false, //否平滑曲线
          showSymbol: false, //未显示鼠标上移的圆点
          symbolSize: 10,
          // symbol:"circle", //设置折线点定位实心点
          itemStyle: {
            normal: {
              color: '#E8892E',
              lineStyle: {
                color: '#E8892E',
              },
            },
            emphasis: {
              color: '#fff',
              borderWidth: 5,
              borderColor: '#E8892E',
            },
          },

          data: this.userdata.totalUserList,
        },
        {
          name: '新增用户',
          type: 'line',
          // stack: 'Total',
          smooth: false, //否平滑曲线
          showSymbol: false, //未显示鼠标上移的圆点
          symbolSize: 10, //圆点大小
          // symbol:"circle", //设置折线点定位实心点
          itemStyle: {
            normal: {
              color: '#6D8A48',
              fontWeigth: 300,
              lineStyle: {
                color: '#6D8A48',
              },
            },
            emphasis: {
              // 圆点颜色
              color: '#fff',
              borderWidth: 5,
              borderColor: '#6D8A48',
            },
          },

          data: this.userdata.newUserList,
        },
      ],
    }
    myChart.setOption(option)
  }
}
</script>
<style scoped>
</style>
