import request from '@/utils/request'

/**
 * 商品管理
 */
export const getProductPage = (params: any) => {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

export const deleteProduct = (ids: string) => {
  return request({
    url: '/product',
    method: 'delete',
    params: { ids }
  })
}

export const editProduct = (params: any) => {
  return request({
    url: '/product',
    method: 'put',
    data: { ...params }
  })
}

export const addProduct = (params: any) => {
  return request({
    url: '/product',
    method: 'post',
    data: { ...params }
  })
}

export const queryProductById = (id: string | (string | null)[]) => {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

export const getProductCategoryList = (params: any) => {
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}

export const queryProductList = (params: any) => {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export const commonDownload = (params: any) => {
  return request({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/common/download',
    method: 'get',
    params
  })
}

export const productStatusByStatus = (params: any) => {
  return request({
    url: `/product/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export const productCategoryList = (params: any) => {
  return request({
    url: '/category/list',
    method: 'get',
    params: { ...params }
  })
}

// compatibility exports for existing views during the light refactor
export const getDishPage = getProductPage
export const deleteDish = deleteProduct
export const editDish = editProduct
export const addDish = addProduct
export const queryDishById = queryProductById
export const getCategoryList = getProductCategoryList
export const queryDishList = queryProductList
export const dishStatusByStatus = productStatusByStatus
export const dishCategoryList = productCategoryList
