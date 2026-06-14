import request from '@/utils/request'

/**
 * 商品分类分页查询。
 */
export const getProductCategoryPage = (params: any) => {
  return request({
    url: '/category/page',
    method: 'get',
    params
  })
}

/**
 * 删除商品分类。
 */
export const deleteProductCategory = (id: string) => {
  return request({
    url: '/category',
    method: 'delete',
    params: { id }
  })
}

/**
 * 更新商品分类。
 */
export const updateProductCategory = (params: any) => {
  return request({
    url: '/category',
    method: 'put',
    data: { ...params }
  })
}

/**
 * 新增商品分类。
 */
export const createProductCategory = (params: any) => {
  return request({
    url: '/category',
    method: 'post',
    data: { ...params }
  })
}

/**
 * 更新商品分类状态。
 */
export const updateProductCategoryStatus = (params: any) => {
  return request({
    url: `/category/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

/**
 * 按类型查询商品分类。
 */
export const listProductCategoryByType = (params: any) => {
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}
