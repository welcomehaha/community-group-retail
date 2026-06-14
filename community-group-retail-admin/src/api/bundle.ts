import request from '@/utils/request'

/**
 * 组合商品管理
 */
export const getBundlePage = (params: any) => {
  return request({
    url: '/bundle/page',
    method: 'GET',
    params
  })
}

export const enableOrDisableBundle = (params: any) => {
  return request({
    url: `/bundle/status/${params.status}`,
    method: 'POST',
    params: { id: params.id }
  })
}

export const deleteBundle = (ids: string) => {
  return request({
    url: '/bundle',
    method: 'DELETE',
    params: { ids }
  })
}

export const editBundle = (params: any) => {
  return request({
    url: '/bundle',
    method: 'put',
    data: { ...params }
  })
}

export const addBundle = (params: any) => {
  return request({
    url: '/bundle',
    method: 'post',
    data: { ...params }
  })
}

export const queryBundleById = (id: string | (string | null)[]) => {
  return request({
    url: `/bundle/${id}`,
    method: 'get'
  })
}

// compatibility exports for existing views during the light refactor
export const getSetmealPage = getBundlePage
export const enableOrDisableSetmeal = enableOrDisableBundle
export const deleteSetmeal = deleteBundle
export const editSetmeal = editBundle
export const addSetmeal = addBundle
export const querySetmealById = queryBundleById
