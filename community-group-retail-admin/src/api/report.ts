import request from '@/utils/request'

/**
 * 报表查询公共时间参数。
 */
export interface ReportDateRangeParams {
  begin: string
  end: string
}

/**
 * 报表接口原始字符串载荷。
 */
interface ReportLineChartResponse {
  dateList: string
}

/**
 * 营业额统计原始响应。
 */
interface ReportTurnoverRawResponse extends ReportLineChartResponse {
  turnoverList: string
}

/**
 * 用户统计原始响应。
 */
interface ReportUserRawResponse extends ReportLineChartResponse {
  totalUserList: string
  newUserList: string
}

/**
 * 订单统计原始响应。
 */
interface ReportOrderRawResponse extends ReportLineChartResponse {
  orderCountList: string
  validOrderCountList: string
  totalOrderCount: number
  validOrderCount: number
  orderCompletionRate: number
}

/**
 * Top10 原始响应。
 */
interface ReportTop10RawResponse {
  nameList: string
  numberList: string
}

/**
 * 营业额统计图表数据。
 */
export interface ReportTurnoverStatistics {
  dateList: string[]
  turnoverList: number[]
}

/**
 * 用户统计图表数据。
 */
export interface ReportUserStatistics {
  dateList: string[]
  totalUserList: number[]
  newUserList: number[]
}

/**
 * 订单统计图表数据。
 */
export interface ReportOrderStatistics {
  dateList: string[]
  orderCountList: number[]
  validOrderCountList: number[]
  totalOrderCount: number
  validOrderCount: number
  orderCompletionRate: number
}

/**
 * 商品销量 Top10 图表数据。
 */
export interface ReportTop10Statistics {
  nameList: string[]
  numberList: number[]
}

/**
 * 将逗号分隔字符串安全转换为字符串数组。
 */
const splitToStringList = (value?: string): string[] => {
  if (!value) {
    return []
  }
  return value.split(',').filter(item => item !== '')
}

/**
 * 将逗号分隔字符串安全转换为数字数组。
 */
const splitToNumberList = (value?: string): number[] => {
  return splitToStringList(value).map(item => Number(item))
}

/**
 * 查询 GMV 统计。
 */
export const getReportTurnoverStatistics = async (params: ReportDateRangeParams): Promise<ReportTurnoverStatistics> => {
  const response = await request({
    url: '/report/turnoverStatistics',
    method: 'get',
    params
  })
  const rawData = (response.data.data || {}) as ReportTurnoverRawResponse
  return {
    dateList: splitToStringList(rawData.dateList),
    turnoverList: splitToNumberList(rawData.turnoverList)
  }
}

/**
 * 查询用户统计。
 */
export const getReportUserStatistics = async (params: ReportDateRangeParams): Promise<ReportUserStatistics> => {
  const response = await request({
    url: '/report/userStatistics',
    method: 'get',
    params
  })
  const rawData = (response.data.data || {}) as ReportUserRawResponse
  return {
    dateList: splitToStringList(rawData.dateList),
    totalUserList: splitToNumberList(rawData.totalUserList),
    newUserList: splitToNumberList(rawData.newUserList)
  }
}

/**
 * 查询订单统计。
 */
export const getReportOrderStatistics = async (params: ReportDateRangeParams): Promise<ReportOrderStatistics> => {
  const response = await request({
    url: '/report/ordersStatistics',
    method: 'get',
    params
  })
  const rawData = (response.data.data || {}) as ReportOrderRawResponse
  return {
    dateList: splitToStringList(rawData.dateList),
    orderCountList: splitToNumberList(rawData.orderCountList),
    validOrderCountList: splitToNumberList(rawData.validOrderCountList),
    totalOrderCount: Number(rawData.totalOrderCount || 0),
    validOrderCount: Number(rawData.validOrderCount || 0),
    orderCompletionRate: Number(rawData.orderCompletionRate || 0)
  }
}

/**
 * 查询商品销量 Top10。
 */
export const getReportTop10 = async (params: ReportDateRangeParams): Promise<ReportTop10Statistics> => {
  const response = await request({
    url: '/report/top10',
    method: 'get',
    params
  })
  const rawData = (response.data.data || {}) as ReportTop10RawResponse
  return {
    nameList: splitToStringList(rawData.nameList).reverse(),
    numberList: splitToNumberList(rawData.numberList).reverse()
  }
}

/**
 * 导出运营报表。
 */
export const exportReport = () => {
  return request({
    url: '/report/export',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 导出运营报表，保留语义化命名供新页面使用。
 */
export const exportOperationReport = exportReport
