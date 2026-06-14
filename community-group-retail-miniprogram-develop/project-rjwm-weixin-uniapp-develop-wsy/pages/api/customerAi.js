import { request } from "../../utils/request.js"

// 校验 AI 客服会话 ID，只允许纯正整数路径参数
const checkConversationId = (conversationId) => {
	const value = String(conversationId)
	if (!/^[1-9]\d*$/.test(value)) {
		throw new Error('会话标识不合法')
	}
}

// 创建 AI 客服会话
export const createCustomerAiSession = (params = {}) => {
	return request({
		url: '/user/ai/customer/session',
		method: 'POST',
		params
	})
}

// 发送 AI 客服消息
export const sendCustomerAiMessage = (params = {}) => {
	return request({
		url: '/user/ai/customer/chat',
		method: 'POST',
		params
	})
}

// 查询 AI 客服历史会话列表
export const getCustomerAiHistoryList = () => {
	return request({
		url: '/user/ai/customer/history',
		method: 'GET'
	})
}

// 查询 AI 客服历史会话详情
export const getCustomerAiHistoryDetail = (conversationId) => {
	checkConversationId(conversationId)
	return request({
		url: `/user/ai/customer/history/${conversationId}`,
		method: 'GET'
	})
}

// 删除 AI 客服历史会话
export const deleteCustomerAiHistory = (conversationId) => {
	checkConversationId(conversationId)
	return request({
		url: `/user/ai/customer/history/${conversationId}`,
		method: 'DELETE'
	})
}
