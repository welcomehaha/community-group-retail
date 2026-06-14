import request from '@/utils/request'

export type OrderId = string | number

export interface OrderApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface OrderListStatistics {
  toBeConfirmed?: number
  confirmed?: number
  deliveryInProgress?: number
}

export interface OrderQueryParams {
  page: number
  pageSize: number
  status?: number
  number?: string
  phone?: string
  beginTime?: string
  endTime?: string
}

export interface OrderActionParams {
  id: OrderId
}

export interface OrderCancelParams extends OrderActionParams {
  cancelReason: string
}

export interface OrderRejectParams extends OrderActionParams {
  rejectionReason: string
}

export interface OrderDetailItem {
  name: string
  number: number
  amount: number
}

export interface OrderSummary {
  id: OrderId
  number: string
  status: number
  orderDishes: string
  address: string
  estimatedDeliveryTime: string
  amount: number
  remark: string
  tablewareNumber: number
  consignee?: string
  phone?: string
  orderTime?: string
  cancelTime?: string
  cancelReason?: string
  deliveryTime?: string
}

export interface OrderDetail extends OrderSummary {
  deliveryTime: string
  cancelReason: string
  rejectionReason: string
  orderDetailList: OrderDetailItem[]
  packAmount: number
  payMethod: number
  checkoutTime: string
}

export interface OrderPageResult {
  records: OrderSummary[]
  total: number
}

export interface OrderDetailQueryParams {
  orderId: OrderId
}

// 查询订单分页列表接口
export const getOrderDetailPage = (params: OrderQueryParams) => {
  return request({
    url: '/order/conditionSearch',
    method: 'get',
    params,
  })
}

// 查询订单详情接口
export const queryOrderDetailById = (params: OrderDetailQueryParams) => {
  return request({
    url: `/order/details/${params.orderId}`,
    method: 'get',
  })
}

// 履约配送接口
export const deliveryOrder = (params: OrderActionParams) => {
  return request({
    url: `/order/delivery/${params.id}`,
    method: 'put',
  })
}

// 完成订单接口
export const completeOrder = (params: OrderActionParams) => {
  return request({
    url: `/order/complete/${params.id}`,
    method: 'put',
  })
}

// 取消订单接口
export const orderCancel = (params: OrderCancelParams) => {
  return request({
    url: '/order/cancel',
    method: 'put',
    data: { ...params },
  })
}

// 履约接单接口
export const orderAccept = (params: OrderActionParams) => {
  return request({
    url: '/order/confirm',
    method: 'put',
    data: { ...params },
  })
}

// 拒绝履约接口
export const orderReject = (params: OrderRejectParams) => {
  return request({
    url: '/order/rejection',
    method: 'put',
    data: { ...params },
  })
}

// 获取待处理、待履约配送、履约中数量接口
export const getOrderListBy = () => {
  return request({
    url: '/order/statistics',
    method: 'get',
  })
}
