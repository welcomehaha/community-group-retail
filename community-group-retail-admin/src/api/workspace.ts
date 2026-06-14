import request from '@/utils/request'

/**
 * 工作台经营数据。
 */
export interface WorkspaceBusinessData {
  turnover: number
  validOrderCount: number
  orderCompletionRate: number
  unitPrice: number
  newUsers: number
}

/**
 * 工作台订单总览数据。
 */
export interface WorkspaceOrderOverview {
  waitingOrders: number
  deliveredOrders: number
  completedOrders: number
  cancelledOrders: number
  allOrders: number
}

/**
 * 工作台商品总览数据。
 */
export interface WorkspaceProductOverview {
  sold: number
  discontinued: number
}

/**
 * 工作台组合商品总览数据。
 */
export interface WorkspaceBundleOverview {
  sold: number
  discontinued: number
}

/**
 * 查询工作台订单总览。
 */
export const getWorkspaceOrderOverview = () => {
  return request({
    url: '/workspace/overviewOrders',
    method: 'get'
  })
}

/**
 * 查询工作台商品总览。
 */
export const getWorkspaceProductOverview = () => {
  return request({
    url: '/workspace/overviewDishes',
    method: 'get'
  })
}

/**
 * 查询工作台组合商品总览。
 */
export const getWorkspaceBundleOverview = () => {
  return request({
    url: '/workspace/overviewSetmeals',
    method: 'get'
  })
}

/**
 * 查询工作台经营数据。
 */
export const getWorkspaceBusinessData = () => {
  return request({
    url: '/workspace/businessData',
    method: 'get'
  })
}
