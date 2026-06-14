import store from './../store'
import { baseUrl } from './env'

// 统一封装小程序请求，所有接口都会拼接本地后端基础地址
export function request({ url = '', params = {}, method = 'GET' }) {
	const storeInfo = store.state
	const header = {
		'Accept': 'application/json',
		'Content-Type': 'application/json',
		// 携带后端 JWT 认证令牌
		'authentication': storeInfo.token
	}

	return new Promise((resolve, reject) => {
		store.commit('setLodding', false)
		uni.request({
			url: baseUrl + url,
			data: params,
			header,
			method,
			success: (res) => {
				const { data } = res
				if (data && (data.code == 200 || data.code === 1)) {
					resolve(data)
					return
				}
				reject(data || { msg: '服务端响应异常' })
			},
			fail: (err) => {
				reject({ msg: err.errMsg || '网络请求失败' })
			}
		})
	})
}
