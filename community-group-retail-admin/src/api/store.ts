import request from '@/utils/request'

/**
 * 查询门店营业状态。
 */
export const getStoreStatus = () => {
  return request({
    url: '/store/status',
    method: 'get'
  })
}

/**
 * 更新门店营业状态。
 */
export const updateStoreStatus = (status: number) => {
  return request({
    url: `/store/${status}`,
    method: 'put',
    data: status
  })
}
