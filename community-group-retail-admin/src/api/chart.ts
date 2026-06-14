import request from '@/utils/request'

/**
 * 获取顶部金额汇总数据。
 */
export const getAmountCollect = (params: any) =>
  request({
    url: `/report/amountCollect/${params.date}`,
    method: 'get'
  })

/**
 * 获取时间范围内营收概况数据。
 */
export const getDayCollect = (params: any) =>
  request({
    url: `/report/dayCollect/${params.start}/${params.end}`,
    method: 'get'
  })

/**
 * 获取 24 小时销售趋势数据。
 */
export const getHourCollect = (params: any) =>
  request({
    url: `/report/hourCollect/${params.type}/${params.date}`,
    method: 'get'
  })

/**
 * 获取单日支付方式构成数据。
 */
export const getPayTypeCollect = (params: any) =>
  request({
    url: `/report/payTypeCollect/${params.date}`,
    method: 'get'
  })

/**
 * 获取单日优惠指标数据。
 */
export const getPrivilegeCollect = (params: any) =>
  request({
    url: `/report/privilegeCollect/${params.date}`,
    method: 'get'
  })

/**
 * 获取单日商品分类销售占比。
 */
export const getCategoryCollect = (params: any) =>
  request({
    url: `/report/categoryCollect/${params.type}/${params.date}`,
    method: 'get'
  })

/**
 * 获取单日商品销售排行。
 */
export const getCurrentProductRank = (params: any) =>
  request({
    url: `/report/currentDishRank/${params.date}`,
    method: 'get'
  })

/**
 * 获取时间范围内销售趋势。
 */
export const getDayAmountCollect = (params: any) =>
  request({
    url: `/report/dayAmountCollect/${params.type}/${params.start}/${params.end}`,
    method: 'get'
  })

/**
 * 获取时间范围内支付方式构成。
 */
export const getDatePayTypeCollect = (params: any) =>
  request({
    url: `/report/datePayTypeCollect/${params.start}/${params.end}`,
    method: 'get'
  })

/**
 * 获取时间范围内商品分类销售占比。
 */
export const getDateCategoryCollect = (params: any) =>
  request({
    url: `/report/dateCategoryCollect/${params.type}/${params.start}/${params.end}`,
    method: 'get'
  })

/**
 * 获取时间范围内商品销售排行。
 */
export const getProductRankForDate = (params: any) =>
  request({
    url: `/report/dishRankForDate/${params.start}/${params.end}`,
    method: 'get'
  })

/**
 * 获取时间范围内优惠指标汇总。
 */
export const getPrivilegeByDate = (params: any) =>
  request({
    url: `/report/privilegeByDate/${params.start}/${params.end}`,
    method: 'get'
  })
