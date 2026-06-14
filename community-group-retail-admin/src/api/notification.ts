import request from '@/utils/request'

/**
 * 分页查询消息通知列表。
 */
export const getNotificationPage = (params: any) => {
  return request({
    url: '/messages/page',
    method: 'get',
    params
  })
}

/**
 * 查询未读消息数量。
 */
export const getUnreadNotificationCount = () => {
  return request({
    url: '/messages/countUnread',
    method: 'get'
  })
}

/**
 * 批量标记消息为已读。
 */
export const batchReadNotification = (data: any) => {
  return request({
    url: '/messages/batch',
    method: 'put',
    data
  })
}

/**
 * 标记单条消息为已读。
 */
export const readNotification = (id: any) => {
  return request({
    url: `/messages/${id}`,
    method: 'PUT'
  })
}
