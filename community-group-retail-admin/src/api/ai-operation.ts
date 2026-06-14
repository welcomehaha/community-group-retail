import request from '@/utils/request'

/**
 * 查询 AI 会话列表。
 */
export const getAiConversationList = () => {
  return request({
    url: '/ai/customer/conversations',
    method: 'get'
  })
}

/**
 * 查询 AI 会话消息明细。
 */
export const getAiMessageList = (conversationId: string) => {
  return request({
    url: `/ai/customer/conversations/${conversationId}/messages`,
    method: 'get'
  })
}

/**
 * 查询 AI 知识库列表。
 */
export const getAiKnowledgeList = (params?: any) => {
  return request({
    url: '/ai/customer/knowledge',
    method: 'get',
    params
  })
}

/**
 * 保存 AI 知识库文档。
 */
export const saveAiKnowledge = (data: any) => {
  return request({
    url: '/ai/customer/knowledge',
    method: 'post',
    data
  })
}

/**
 * 更新 AI 知识库文档状态。
 */
export const updateAiKnowledgeStatus = (id: number | string, status: number) => {
  return request({
    url: `/ai/customer/knowledge/${id}/status/${status}`,
    method: 'put'
  })
}

/**
 * 查询 AI 工具调用日志。
 */
export const getAiToolLogList = (params?: any) => {
  return request({
    url: '/ai/customer/tool-logs',
    method: 'get',
    params
  })
}

/**
 * 查询热门问题统计。
 */
export const getAiHotQuestionList = (limit?: number) => {
  return request({
    url: '/ai/customer/statistics/hot-questions',
    method: 'get',
    params: {
      limit
    }
  })
}

/**
 * 查询未命中问题列表。
 */
export const getAiFallbackQuestionList = (limit?: number) => {
  return request({
    url: '/ai/customer/statistics/fallback-questions',
    method: 'get',
    params: {
      limit
    }
  })
}

/**
 * 查询未命中问题趋势。
 */
export const getAiFallbackTrend = (limit?: number) => {
  return request({
    url: '/ai/customer/statistics/fallback-trend',
    method: 'get',
    params: {
      limit
    }
  })
}

/**
 * 查询高风险审计列表。
 */
export const getAiRiskAuditList = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits',
    method: 'get',
    params
  })
}

/**
 * 分页查询高风险审计列表。
 */
export const getAiRiskAuditPage = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/page',
    method: 'get',
    params
  })
}

/**
 * 查询高风险审计汇总。
 */
export const getAiRiskAuditSummary = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/summary',
    method: 'get',
    params
  })
}

/**
 * 查询高风险动作审批概览。
 */
export const getAiRiskApprovalOverview = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/approval-overview',
    method: 'get',
    params
  })
}

/**
 * 查询高风险动作审批趋势。
 */
export const getAiRiskApprovalTrend = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/approval-trend',
    method: 'get',
    params
  })
}

/**
 * 查询高风险动作类型趋势。
 */
export const getAiRiskActionTrend = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/action-trend',
    method: 'get',
    params
  })
}

/**
 * 查询高风险动作审批通过率统计。
 */
export const getAiRiskActionApprovalStats = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/action-approval-stats',
    method: 'get',
    params
  })
}

/**
 * 导出高风险审计数据。
 */
export const exportAiRiskAudits = (params?: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 更新高风险审计处理状态。
 */
export const updateAiRiskAudit = (data: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits',
    method: 'put',
    data
  })
}

/**
 * 更新高风险动作审批状态。
 */
export const updateAiRiskApproval = (data: any) => {
  return request({
    url: '/ai/customer/statistics/risk-audits/approval',
    method: 'put',
    data
  })
}
