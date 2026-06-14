import { request } from "../../utils/request.js"

// 开桌
export const openTable = (params) =>
	request({
		url: `/user/table/open/${params.tableId}/${params.seatNumber}`,
		method: 'GET',
		params
	})

// 获取桌台状态
export const getTableState = (params) =>
	request({
		url: `/user/table/tableStatus/${params.shopId}/${params.storeId}/${params.tableId}`,
		method: 'GET',
		params,
	})

// 获取购物车商品
export const getTableOrderDishList = (params) =>
	request({
		url: `/user/order/shopCart//${params.tableId}`,
		method: 'GET',
		params
	})
// 获取商品规格
export const getMoreNorm = (params) =>
	request({
		url: `/user/dish/flavor/${params.dishId}`,
		method: 'GET',
		params,
	})

// 获取商品分类列表	
export const getList = (params) =>
	request({
		url: `/user/dish/category`,
		method: 'GET',
		params,
	})

// 获取商品详情	
export const getDishDetail = (params) =>
	request({
		url: `/user/dish/setmealDishList/${params.setmealId}`,
		method: 'GET',
		params,
	})

// 根据分类获取商品列表
export const getDishList = (params) =>
	request({
		url: `/user/dish/dishPageList/${params.categoryId}/${params.type}/${params.page}/${params.pageSize}`,
		method: 'GET',
		params
	})

// 加商品
export const addDish = (params) =>
	request({
		url: `/user/order/addDish`,
		method: 'POST',
		params
	})

// 减商品
export const delDish = (params) =>
	request({
		url: `/user/order/decreaseDish/${params.tableId}/${params.dishId}`,
		method: 'GET',
		params
	})

// 清空购物车
export const clearOrder = (params) =>
	request({
		url: `/user/order/cleanShopCart/${params.tableId}`,
		method: 'GET',
		params
	})

// 提交订单 
export const payOrder = (params) =>
	request({
		url: `/user/order/pay/${params.tableId}/${params.jsCode}`,
		method: 'GET',
		params
	})


// 用户登录
export const userLogin = (params) => {
	return request({
		url: '/user/user/login',
		method: 'POST',
		params
	})
}

// 商品和组合商品的分类
export const getCategoryList = (params) => {
	return request({
		url: '/user/category/list',
		method: 'GET',
		params
	})
}

// 查询商品管理列表
export const dishListByCategoryId = (params) => {
	return request({
		url: '/user/product/list',
		method: 'GET',
		params
	})
}

// 文件下载---预览
export const commonDownload = (params) => {
	return request({
		url: '/user/common/download',
		method: 'GET',
		params
	})
}


// 购物车----加商品功能实现
export const addShoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart',
		method: 'POST',
		params
	})
}

// 根据type类型查询是组合商品的接口
export const querySetmeaList = (params) => {
	return request({
		url: '/user/bundle/list',
		method: 'GET',
		params
	})
}

// 获取购物车集合
export const getShoppingCartList = (params) => {
	return request({
		url: '/user/shoppingCart/list',
		method: 'GET',
		params
	})
}


// 修改购物车数量功能
export const editHoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart',
		method: 'PUT',
		params
	})
}


// 购物车新增接口-new
export const newAddShoppingCartAdd = (params) => {
	return request({
		url: '/user/shoppingCart/add',
		method: 'POST',
		params
	})
}


// 购物车减商品接口-new 
export const newShoppingCartSub = (params) => {
	return request({
		url: '/user/shoppingCart/sub',
		method: 'POST',
		params
	})
}


// 清除购物车
export const delShoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart/clean',
		method: 'DELETE',
		params
	})
}


// 最近订单和历史订单
export const queryOrderUserPage = (params) => {
	return request({
		url: '/user/order/userPage',
		method: 'GET',
		params
	})
}


// 用户下单
export const submitOrderSubmit = (params) => {
	return request({
		url: '/user/order/submit',
		method: 'POST',
		params
	})
}


// 查询地址列表
export const queryAddressBookList = (params) => {
	return request({
		url: '/user/addressBook/list',
		method: 'GET',
		params
	})
}

// 新增默认接口
export const putAddressBookDefault = (params) => {
	return request({
		url: '/user/addressBook/default',
		method: 'PUT',
		params
	})
}


// 新增地址接口
export const addAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'POST',
		params
	})
}

// 修改地址接口
export const editAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'PUT',
		params
	})
}

// 删除地址
export const delAddressBook = (id) => {
	return request({
		url: `/user/addressBook/?id=${id}`,
		method: 'DELETE',
		params: { id }
	})
}

// 查询地址通过id
export const queryAddressBookById = (params) => {
	return request({
		url: `/user/addressBook/${params.id}`,
		method: 'GET',
		params
	})
}


// 再来一单
export const oneOrderAgain = (params) => {
	return request({
		url: '/user/order/again',
		method: 'POST',
		params
	})
}

// 查询默认地址
export const getAddressBookDefault = () => {
	return request({
		url: '/user/addressBook/default',
		method: 'GET'
	})
}


// 此接口为首页查询组合商品详情展示的接口
export const querySetmealDishById = (params) => {
	return request({
		url: `/user/bundle/product/${params.id}`,
		method: 'GET'
	})
}
// v2.0添加接口
// 获取首页门店信息
export const getShopStatus = (params) => {
	return request({
		url: `/user/store/status`,
		method: 'GET'
	})
}
// 获取门店信息
export const getMerchantInfo = (params) => {
	return request({
		url: `/user/shop/getMerchantInfo`,
		method: 'GET'
	})
}
// 历史订单
export const getOrderPage = (params) => {
	return request({
		url: '/user/order/historyOrders',
		method: 'GET',
		params
	})
}
// 订单详情
export const getOrderDetail = (params) =>
	request({
		url: `/user/order/orderDetail/${params}`,
		method: 'GET'
	})
// 取消订单
export const cancelOrder = (params) =>
	request({
		url: `/user/order/cancel/${params}`,
		method: 'PUT'
	})
// 催单
export const reminderOrder = (params) =>
	request({
		url: `/user/order/reminder/${params}`,
		method: 'GET'
	})
// 订单支付
export const paymentOrder = (params) =>
	request({
		url: `/user/order/payment`,
		method: 'PUT',
		params
	})
// 再来一单
export const repetitionOrder = (params) =>
	request({
		url: `/user/order/repetition/${params}`,
		method: 'POST',
		params
	})


// 获取用户配送期望时间
export const getEstimatedDeliveryTime = (params) =>
	request({
		url: `/user/order/getEstimatedDeliveryTime`,
		method: 'get',
		params
	})
// 查询用户订单支付状态列表信息
export const queryOrdersCheckStatus = (params) =>
	request({
		url: `/user/order/queryOrdersCheckStatus`,
		method: 'get',
		params
	})


