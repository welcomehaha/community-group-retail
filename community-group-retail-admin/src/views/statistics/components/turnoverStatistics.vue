<template>
  <div class="container">
    <h2 class="homeTitle">
      GMV统计
    </h2>
    <div class="charBox">
      <div id="main" style="width: 100%; height: 320px" />
      <ul class="orderListLine turnover">
        <li>GMV(元)</li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import * as echarts from 'echarts'
import { ReportTurnoverStatistics } from '@/api/report'
@Component({
  name: 'TurnoverStatistics',
})
export default class extends Vue {
  @Prop() private turnoverdata!: ReportTurnoverStatistics
  @Watch('turnoverdata')
  getData() {
    this.$nextTick(() => {
      this.initChart()
    })
  }
  initChart() {
    const chartDom = document.getElementById('main')
    if (!chartDom) {
      return
    }
    const myChart = echarts.init(chartDom)

    const option: any = {
      // title: {
      //   text: 'GMV(元)',
      //   top: 'bottom',
      //   left: 'center',
      //   textAlign: 'center',
      //   textStyle: {
      //     fontSize: 12,
      //     fontWeight: 'normal',
      //   },
      // },
      tooltip: {
        trigger: 'axis',
      },
      grid: {
        top: '5%',
        left: '10',
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
        data: this.turnoverdata.dateList, //后端传来的动态数据
      },
      yAxis: [
        {
          type: 'value',
          min: 0,
          //max: 50000,
          //interval: 1000,
          axisLabel: {
            textStyle: {
              color: '#666',
              fontSize: '12px',
            }
            // formatter: "{value} ml",//单位
          }
        }
      ],
      series: [
        {
          name: 'GMV',
          type: 'line',
          // stack: 'Total',
          smooth: false, //否平滑曲线
          showSymbol: false, //未显示鼠标上移的圆点
          symbolSize: 10,
          // symbol:"circle", //设置折线点定位实心点
          itemStyle: {
            normal: {
              color: '#F29C1B',
              lineStyle: {
                color: '#FFD000',
              },
            },
            emphasis: {
              color: '#fff',
              borderWidth: 5,
              borderColor: '#FFC100',
            },
          },

          data: this.turnoverdata.turnoverList,
        },
      ],
    }
    myChart.setOption(option)
  }
}
</script>
