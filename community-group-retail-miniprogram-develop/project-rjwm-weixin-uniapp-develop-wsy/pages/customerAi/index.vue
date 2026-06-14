<template>
	<view class="customer-ai-page">
		<view class="customer-ai-header">
			<view class="header-left">
				<text class="header-title">在线客服</text>
				<text class="header-subtitle">支持订单咨询、配送范围、优惠券使用、催履约、取消未支付订单、售后申请</text>
			</view>
			<view class="header-actions">
				<text class="header-action" @click="toggleHistory">历史会话</text>
				<text class="header-action danger" @click="deleteConversation">删除会话</text>
			</view>
		</view>

		<view v-if="historyVisible" class="history-panel">
			<view class="history-panel-header">
				<text class="history-title">会话记录</text>
				<text class="history-new" @click="createNewConversation">新建会话</text>
			</view>
			<scroll-view scroll-y class="history-list">
				<view
					v-for="item in historyList"
					:key="item.conversationId"
					class="history-item"
					:class="{ active: item.conversationId === conversationId }"
					@click="loadHistoryDetail(item.conversationId)"
				>
					<text class="history-item-title">{{ item.title || '在线客服' }}</text>
					<text class="history-item-desc">{{ item.lastMessage || '暂无消息' }}</text>
				</view>
				<view v-if="historyList.length === 0" class="history-empty">暂无历史会话</view>
			</scroll-view>
		</view>

		<view class="quick-question-bar">
			<view v-if="contextOrderNumber" class="context-bar">
				<text class="context-label">当前订单</text>
				<text class="context-value">{{ contextOrderNumber }}</text>
			</view>
			<text class="quick-label">快捷提问</text>
			<scroll-view scroll-x class="quick-scroll">
				<view
					v-for="item in quickQuestions"
					:key="item.label"
					class="quick-item"
					@click="sendQuickQuestion(item.question)"
				>
					<text class="quick-item-label">{{ item.label }}</text>
					<text class="quick-item-text">{{ item.question }}</text>
				</view>
			</scroll-view>
		</view>

		<scroll-view scroll-y class="message-scroll" :scroll-into-view="scrollIntoView">
			<view v-if="initializing" class="status-box">正在初始化客服会话...</view>
				<view v-if="initFailed" class="init-failed-box">
					<text class="init-failed-title">客服会话初始化失败</text>
					<text class="init-failed-desc">请检查网络或登录状态后重试。</text>
					<button class="retry-button" size="mini" @click="retryInitPage">重新连接</button>
				</view>

			<view
				v-for="item in messages"
				:id="`msg-${item.id}`"
				:key="item.id"
				class="message-row"
				:class="item.role === 'user' ? 'message-row-user' : 'message-row-assistant'"
			>
				<view class="message-bubble" :class="item.role === 'user' ? 'message-bubble-user' : 'message-bubble-assistant'">
					<text class="message-content">{{ item.content }}</text>
					<view v-if="item.references && item.references.length > 0" class="message-reference">
						<text class="message-reference-title">参考来源</text>
						<text v-for="reference in item.references" :key="reference" class="message-reference-item">{{ reference }}</text>
					</view>
				</view>
			</view>

			<view v-if="sending" class="status-box">客服 AI 正在处理中...</view>
		</scroll-view>

		<view class="input-area" :style="{ paddingBottom: keyboardHeight ? keyboardHeight + 'px' : '' }">
			<input
				v-model="inputValue"
				class="input-box"
				placeholder="请输入订单号、商品名称、配送地址问题或优惠券问题"
				maxlength="500"
				confirm-type="send"
				@keyboardheightchange="onKeyboardHeightChange"
				@confirm="sendMessage"
			/>
			<button class="send-button" type="primary" :loading="sending" :disabled="sending || initFailed || !conversationId" @click="sendMessage">发送</button>
		</view>
	</view>
</template>

<script>
import {
	createCustomerAiSession,
	sendCustomerAiMessage,
	getCustomerAiHistoryList,
	getCustomerAiHistoryDetail,
	deleteCustomerAiHistory
} from '../api/customerAi.js'

export default {
	data() {
		return {
			conversationId: null,
			messages: [],
			inputValue: '',
			contextOrderNumber: '',
			sending: false,
			initializing: true,
			initFailed: false,
			loadingHistory: false,
			historyVisible: false,
			historyList: [],
			scrollIntoView: '',
			keyboardHeight: 0,
			quickQuestions: [
				{ label: '订单进度', question: '我的订单现在到哪里了？' },
				{ label: '配送范围', question: '门店现在营业吗，我这里能送到吗？' },
				{ label: '优惠券使用', question: '新人优惠券怎么用？' },
				{ label: '催履约处理', question: '可以帮我催一下履约进度吗？' }
			]
		}
	},
	onLoad(options = {}) {
		const conversationId = options.conversationId ? Number(options.conversationId) : null
		const orderNumber = options.orderNumber ? decodeURIComponent(options.orderNumber) : ''
		const intent = options.intent ? decodeURIComponent(options.intent) : ''
		this.contextOrderNumber = orderNumber
		this.inputValue = this.buildPresetQuestion(orderNumber, intent)
		this.initPage(conversationId)
	},
	onShow() {
		this.loadHistoryList()
	},
	onUnload() {
		this.inputValue = ''
		this.keyboardHeight = 0
	},
	methods: {
		// 根据订单号和咨询意图生成预设问题
		buildPresetQuestion(orderNumber, intent) {
			if (!orderNumber) {
				return ''
			}
			if (intent === 'reminder') {
				return `我想咨询订单号${orderNumber}的履约进度，如果符合条件也想继续催履约。`
			}
			if (intent === 'delivery_scope') {
				return `我想咨询订单号${orderNumber}对应地址的配送范围和门店营业情况。`
			}
			if (intent === 'after_sale') {
				return `我想咨询订单号${orderNumber}的售后处理方式和申请流程。`
			}
			return `我想咨询订单号${orderNumber}的订单进度、配送情况和售后处理。`
		},
		// 初始化页面会话
		async initPage(preferredConversationId) {
			this.initializing = true
			this.initFailed = false
			try {
				const historyRes = await getCustomerAiHistoryList()
				const historyList = this.extractList(historyRes)
				let nextConversationId = preferredConversationId
				if (!nextConversationId && historyList.length > 0) {
					nextConversationId = historyList[0].conversationId
				}
				this.historyList = historyList
				if (nextConversationId) {
					const loaded = await this.loadHistoryDetail(nextConversationId)
					if (!loaded) {
						await this.createSession('在线客服')
					}
				} else {
					await this.createSession('在线客服')
				}
			} catch (error) {
				uni.showToast({ title: this.getErrorMessage(error, '初始化客服失败'), icon: 'none' })
				try {
					await this.createSession('在线客服')
				} catch (sessionError) {
					this.conversationId = null
					this.messages = []
					this.initFailed = true
					uni.showToast({ title: this.getErrorMessage(sessionError, '客服暂不可用，请稍后重试'), icon: 'none' })
				}
			} finally {
				this.initializing = false
			}
		},
		// 重新初始化客服会话
		retryInitPage() {
			this.initPage(this.conversationId)
		},
		// 新建客服会话并展示欢迎语
		async createSession(title = '在线客服') {
			const res = await createCustomerAiSession({ title })
			if (!res || res.code !== 1 || !res.data || !res.data.conversationId) {
				throw new Error((res && res.msg) || '创建会话失败')
			}
			this.conversationId = res.data.conversationId
			this.messages = [
				{
					id: 'welcome',
					role: 'assistant',
					content: '您好，这里是社区团购即时零售客服 AI。您可以直接咨询订单进度、门店营业、配送范围、优惠券使用、催履约、取消未支付订单或售后申请。',
					createTime: ''
				}
			]
			this.scrollToBottom()
			await this.loadHistoryList()
		},
		// 查询历史会话详情
		async loadHistoryDetail(conversationId) {
			if (!conversationId) {
				return false
			}
			if (conversationId === this.conversationId && this.historyVisible) {
				this.historyVisible = false
				return true
			}
			this.loadingHistory = true
			try {
				const res = await getCustomerAiHistoryDetail(conversationId)
				const messages = this.extractList(res).map((item, index) => ({
					id: item.id || `msg-${index}`,
					role: item.role || 'assistant',
					content: item.content || '',
					createTime: item.createTime || '',
					toolName: item.toolName || '',
					toolResult: item.toolResult || '',
					references: item.references || []
				}))
				this.conversationId = conversationId
				this.historyVisible = false
				this.messages = messages.length > 0 ? messages : [
					{ id: 'empty', role: 'assistant', content: '当前会话暂无消息，您可以直接输入问题开始咨询。', createTime: '' }
				]
				this.scrollToBottom()
				return true
			} catch (error) {
				uni.showToast({ title: this.getErrorMessage(error, '加载会话失败'), icon: 'none' })
				return false
			} finally {
				this.loadingHistory = false
			}
		},
		// 查询历史会话列表
		async loadHistoryList() {
			try {
				const res = await getCustomerAiHistoryList()
				this.historyList = this.extractList(res)
			} catch (error) {
				console.warn('loadHistoryList error', error)
			}
		},
		// 键盘高度变化时适配输入区域
		onKeyboardHeightChange(event) {
			this.keyboardHeight = event.detail.height || 0
		},
		// 点击快捷问题后直接发送
		sendQuickQuestion(question) {
			this.inputValue = question || ''
			this.sendMessage()
		},
		// 发送用户消息并追加 AI 回复
		async sendMessage() {
			const message = (this.inputValue || '').trim()
			if (!message) {
				uni.showToast({ title: '请输入咨询内容', icon: 'none' })
				return
			}
			if (message.length > 500) {
				uni.showToast({ title: '单次输入不能超过500字', icon: 'none' })
				return
			}
			if (this.initFailed) {
				uni.showToast({ title: '请先重新连接客服会话', icon: 'none' })
				return
			}
			if (!this.conversationId || this.sending) {
				return
			}
			const requestConversationId = this.conversationId
			this.messages = this.messages.concat([{ id: `user-${Date.now()}`, role: 'user', content: message, createTime: '' }])
			this.inputValue = ''
			this.sending = true
			this.scrollToBottom()
			try {
				const res = await sendCustomerAiMessage({ conversationId: requestConversationId, message })
				if (this.conversationId !== requestConversationId) {
					return
				}
				if (!res || res.code !== 1 || !res.data) {
					throw new Error((res && res.msg) || '发送失败')
				}
				this.messages = this.messages.concat([
					{
						id: `assistant-${Date.now()}`,
						role: 'assistant',
						content: res.data.answer || '当前没有生成有效回复，请稍后重试。',
						createTime: '',
						route: res.data.route || '',
						references: res.data.references || []
					}
				])
				this.scrollToBottom()
				await this.loadHistoryList()
			} catch (error) {
				if (this.conversationId !== requestConversationId) {
					return
				}
				this.messages = this.messages.concat([
					{ id: `error-${Date.now()}`, role: 'assistant', content: this.getErrorMessage(error, '当前服务繁忙，请稍后再试。'), createTime: '' }
				])
				this.scrollToBottom()
			} finally {
				this.sending = false
			}
		},
		// 删除当前会话
		deleteConversation() {
			if (!this.conversationId) {
				return
			}
			uni.showModal({
				title: '删除当前会话',
				content: '删除后当前聊天记录不可恢复，是否继续？',
				confirmColor: '#f56c6c',
				success: async (modalRes) => {
					if (!modalRes.confirm) {
						return
					}
					uni.showLoading({ title: '删除中', mask: true })
					try {
						const res = await deleteCustomerAiHistory(this.conversationId)
						if (!res || res.code !== 1) {
							throw new Error((res && res.msg) || '删除失败')
						}
						await this.createSession('在线客服')
						uni.showToast({ title: '会话已删除', icon: 'success' })
					} catch (error) {
						uni.showToast({ title: this.getErrorMessage(error, '删除失败'), icon: 'none' })
					} finally {
						uni.hideLoading()
					}
				}
			})
		},
		// 切换历史会话面板
		toggleHistory() {
			this.historyVisible = !this.historyVisible
		},
		// 新建一轮客服会话
		async createNewConversation() {
			uni.showLoading({ title: '新建中', mask: true })
			try {
				await this.createSession('在线客服')
				this.historyVisible = false
				uni.showToast({ title: '已新建会话', icon: 'success' })
			} catch (error) {
				uni.showToast({ title: this.getErrorMessage(error, '新建失败'), icon: 'none' })
			} finally {
				uni.hideLoading()
			}
		},
		// 滚动到最新消息
		scrollToBottom() {
			const lastMessage = this.messages[this.messages.length - 1]
			if (!lastMessage) {
				return
			}
			this.$nextTick(() => {
				this.scrollIntoView = `msg-${lastMessage.id}`
			})
		},
		// 兼容 Result<List> 数据结构
		extractList(res) {
			if (res && res.code === 1 && Array.isArray(res.data)) {
				return res.data
			}
			return []
		},
		// 提取用户友好的错误文案
		getErrorMessage(error, fallbackMessage) {
			if (!error) {
				return fallbackMessage
			}
			return error.message || error.msg || fallbackMessage
		}
	}
}
</script>

<style lang="scss" scoped>
.customer-ai-page {
	min-height: 100vh;
	background: #f5f7fa;
	display: flex;
	flex-direction: column;
}

.customer-ai-header {
	padding: 32rpx 28rpx 24rpx;
	background: linear-gradient(135deg, #ffc200 0%, #ff9f1a 100%);
}

.header-left {
	display: flex;
	flex-direction: column;
}

.header-title {
	font-size: 36rpx;
	font-weight: 600;
	color: #333333;
	line-height: 52rpx;
}

.header-subtitle {
	margin-top: 8rpx;
	font-size: 24rpx;
	color: #5a4300;
	line-height: 36rpx;
}

.header-actions {
	display: flex;
	justify-content: flex-end;
	margin-top: 20rpx;
}

.header-action {
	margin-left: 24rpx;
	font-size: 24rpx;
	color: #333333;
}

.header-action.danger {
	color: #b42318;
}

.history-panel {
	margin: 20rpx 24rpx 0;
	padding: 24rpx;
	background: #ffffff;
	border-radius: 16rpx;
}

.history-panel-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.history-title,
.history-new {
	font-size: 26rpx;
	line-height: 40rpx;
}

.history-title {
	color: #333333;
	font-weight: 600;
}

.history-new {
	color: #e95f3c;
}

.history-list {
	max-height: 320rpx;
	margin-top: 16rpx;
}

.history-item {
	padding: 18rpx 0;
	border-bottom: 1rpx solid #ebeef5;
}

.history-item.active .history-item-title {
	color: #e95f3c;
}

.history-item-title,
.history-item-desc,
.history-empty {
	display: block;
}

.history-item-title {
	font-size: 26rpx;
	color: #303133;
	line-height: 40rpx;
}

.history-item-desc,
.history-empty {
	margin-top: 8rpx;
	font-size: 22rpx;
	color: #909399;
	line-height: 32rpx;
}

.quick-question-bar {
	padding: 20rpx 24rpx 12rpx;
}

.context-bar {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
}

.context-label {
	font-size: 22rpx;
	color: #909399;
	line-height: 32rpx;
}

.context-value {
	margin-left: 12rpx;
	padding: 8rpx 18rpx;
	font-size: 22rpx;
	color: #e95f3c;
	line-height: 32rpx;
	background: #fff4f1;
	border-radius: 999rpx;
}

.quick-label {
	display: block;
	margin-bottom: 12rpx;
	font-size: 24rpx;
	color: #606266;
}

.quick-scroll {
	white-space: nowrap;
}

.quick-item {
	display: inline-flex;
	flex-direction: column;
	vertical-align: top;
	min-width: 220rpx;
	margin-right: 16rpx;
	padding: 16rpx 20rpx;
	color: #e95f3c;
	background: #fff4f1;
	border: 1rpx solid #ffd2c7;
	border-radius: 24rpx;
	box-sizing: border-box;
}

.quick-item-label {
	font-size: 22rpx;
	font-weight: 600;
	line-height: 32rpx;
	color: #c2410c;
}

.quick-item-text {
	margin-top: 8rpx;
	font-size: 22rpx;
	line-height: 32rpx;
	color: #e95f3c;
	white-space: normal;
}

.message-scroll {
	flex: 1;
	padding: 12rpx 24rpx 24rpx;
	box-sizing: border-box;
}

.message-row {
	display: flex;
	margin-bottom: 20rpx;
}

.message-row-user {
	justify-content: flex-end;
}

.message-row-assistant {
	justify-content: flex-start;
}

.message-bubble {
	max-width: 80%;
	padding: 20rpx 22rpx;
	border-radius: 20rpx;
	box-sizing: border-box;
}

.message-bubble-user {
	background: #e95f3c;
}

.message-bubble-assistant {
	background: #ffffff;
}

.message-content {
	font-size: 28rpx;
	line-height: 42rpx;
	color: #303133;
	word-break: break-all;
}

.message-bubble-user .message-content {
	color: #ffffff;
}

.message-reference {
	margin-top: 14rpx;
	padding-top: 14rpx;
	border-top: 1rpx solid rgba(0, 0, 0, 0.08);
}

.message-reference-title,
.message-reference-item {
	display: block;
	font-size: 22rpx;
	line-height: 32rpx;
	color: #909399;
}

.status-box {
	margin: 20rpx auto;
	padding: 16rpx 24rpx;
	font-size: 24rpx;
	color: #909399;
	text-align: center;
}

.init-failed-box {
	margin: 48rpx 24rpx;
	padding: 32rpx 24rpx;
	background: #ffffff;
	border-radius: 16rpx;
	text-align: center;
}

.init-failed-title,
.init-failed-desc {
	display: block;
}

.init-failed-title {
	font-size: 30rpx;
	font-weight: 600;
	color: #303133;
	line-height: 44rpx;
}

.init-failed-desc {
	margin-top: 12rpx;
	font-size: 24rpx;
	color: #909399;
	line-height: 36rpx;
}

.retry-button {
	margin-top: 24rpx;
	background: #e95f3c !important;
	color: #ffffff !important;
}

.input-area {
	display: flex;
	align-items: center;
	padding: 18rpx 24rpx calc(18rpx + env(safe-area-inset-bottom));
	background: #ffffff;
	border-top: 1rpx solid #ebeef5;
	box-sizing: border-box;
}

.input-box {
	flex: 1;
	height: 76rpx;
	padding: 0 24rpx;
	font-size: 26rpx;
	background: #f5f7fa;
	border-radius: 38rpx;
	box-sizing: border-box;
}

.send-button {
	width: 132rpx;
	height: 76rpx;
	margin-left: 16rpx;
	line-height: 76rpx;
	font-size: 26rpx;
	background: #e95f3c !important;
	border-radius: 38rpx;
}
</style>
