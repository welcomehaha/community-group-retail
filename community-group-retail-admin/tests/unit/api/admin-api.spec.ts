import request from '@/utils/request'
import { login, logout, updatePassword } from '@/api/auth'
import {
  getEmployeeList,
  enableOrDisableEmployee,
  createEmployeeWithRoles,
  getEmployeeById,
  updateEmployee,
  getEmployeeRoleOptions,
  getEmployeePermissionInfo,
  assignEmployeeRoles
} from '@/api/employee'
import {
  getAiConversationList,
  getAiMessageList,
  getAiKnowledgeList,
  saveAiKnowledge,
  updateAiKnowledgeStatus,
  getAiToolLogList,
  getAiHotQuestionList,
  getAiFallbackQuestionList,
  getAiFallbackTrend,
  getAiRiskAuditPage,
  getAiRiskAuditSummary,
  getAiRiskApprovalOverview,
  getAiRiskApprovalTrend,
  getAiRiskActionTrend,
  getAiRiskActionApprovalStats,
  exportAiRiskAudits,
  updateAiRiskAudit,
  updateAiRiskApproval
} from '@/api/ai-operation'
import {
  getReportTurnoverStatistics,
  getReportUserStatistics,
  getReportOrderStatistics,
  getReportTop10,
  exportReport
} from '@/api/report'
import {
  getNotificationPage,
  getUnreadNotificationCount,
  batchReadNotification,
  readNotification
} from '@/api/notification'

jest.mock('@/utils/request', () => jest.fn((config) => Promise.resolve(config)))

const requestMock = request as jest.MockedFunction<typeof request>

describe('Admin API Modules', () => {
  beforeEach(() => {
    requestMock.mockClear()
  })

  it('auth.ts 应正确封装登录、退出和改密请求', async () => {
    const loginPayload = { username: 'admin', password: '123456' }
    const passwordPayload = { oldPassword: '123456', newPassword: '654321' }

    await login(loginPayload)
    expect(requestMock).toHaveBeenNthCalledWith(1, {
      url: '/employee/login',
      method: 'post',
      data: loginPayload
    })

    await logout()
    expect(requestMock).toHaveBeenNthCalledWith(2, {
      url: '/employee/logout',
      method: 'post'
    })

    await updatePassword(passwordPayload)
    expect(requestMock).toHaveBeenNthCalledWith(3, {
      url: '/employee/editPassword',
      method: 'put',
      data: passwordPayload
    })
  })

  it('employee.ts 应只承载员工资料与授权接口', async () => {
    const listParams = { page: 1, pageSize: 10 }
    const employeePayload = { id: 1, name: '张三' }
    const rolePayload = { employeeId: 1, roleIds: [1, 2] }

    await getEmployeeList(listParams)
    await enableOrDisableEmployee({ id: 1, status: 0 })
    await createEmployeeWithRoles({ ...employeePayload, roleIds: [1] })
    await getEmployeeById(1)
    await updateEmployee(employeePayload)
    await getEmployeeRoleOptions()
    await getEmployeePermissionInfo(1)
    await assignEmployeeRoles(1, rolePayload)

    expect(requestMock).toHaveBeenNthCalledWith(1, {
      url: '/employee/page',
      method: 'get',
      params: listParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(2, {
      url: '/employee/status/0',
      method: 'post',
      params: { id: 1 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(3, {
      url: '/employee/with-roles',
      method: 'post',
      data: { ...employeePayload, roleIds: [1] }
    })
    expect(requestMock).toHaveBeenNthCalledWith(4, {
      url: '/employee/1',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(5, {
      url: '/employee',
      method: 'put',
      data: employeePayload
    })
    expect(requestMock).toHaveBeenNthCalledWith(6, {
      url: '/employee/roles/options',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(7, {
      url: '/employee/1/permissions',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(8, {
      url: '/employee/1/roles',
      method: 'put',
      data: rolePayload
    })
  })

  it('ai-operation.ts 应映射到真实 AI 运营接口', async () => {
    await getAiConversationList()
    await getAiMessageList('99')
    await getAiKnowledgeList({ keyword: '售后' })
    await saveAiKnowledge({ title: 'FAQ' })
    await updateAiKnowledgeStatus(10, 1)
    await getAiToolLogList()
    await getAiHotQuestionList(10)
    await getAiFallbackQuestionList(10)
    await getAiFallbackTrend(7)
    await getAiRiskAuditPage({ page: 1 })
    await getAiRiskAuditSummary({ auditStatus: 0 })
    await getAiRiskApprovalOverview({ approvalStatus: 0 })
    await getAiRiskApprovalTrend({ actionType: 'cancel_order' })
    await getAiRiskActionTrend({ actionType: 'after_sale' })
    await getAiRiskActionApprovalStats({ actionType: 'remind_order' })
    await exportAiRiskAudits({ page: 1, pageSize: 100 })
    await updateAiRiskAudit({ assistantMessageId: 1, auditStatus: 1 })
    await updateAiRiskApproval({ assistantMessageId: 1, approvalStatus: 1 })

    expect(requestMock).toHaveBeenNthCalledWith(1, {
      url: '/ai/customer/conversations',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(2, {
      url: '/ai/customer/conversations/99/messages',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(3, {
      url: '/ai/customer/knowledge',
      method: 'get',
      params: { keyword: '售后' }
    })
    expect(requestMock).toHaveBeenNthCalledWith(4, {
      url: '/ai/customer/knowledge',
      method: 'post',
      data: { title: 'FAQ' }
    })
    expect(requestMock).toHaveBeenNthCalledWith(5, {
      url: '/ai/customer/knowledge/10/status/1',
      method: 'put'
    })
    expect(requestMock).toHaveBeenNthCalledWith(6, {
      url: '/ai/customer/tool-logs',
      method: 'get',
      params: undefined
    })
    expect(requestMock).toHaveBeenNthCalledWith(7, {
      url: '/ai/customer/statistics/hot-questions',
      method: 'get',
      params: { limit: 10 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(8, {
      url: '/ai/customer/statistics/fallback-questions',
      method: 'get',
      params: { limit: 10 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(9, {
      url: '/ai/customer/statistics/fallback-trend',
      method: 'get',
      params: { limit: 7 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(10, {
      url: '/ai/customer/statistics/risk-audits/page',
      method: 'get',
      params: { page: 1 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(11, {
      url: '/ai/customer/statistics/risk-audits/summary',
      method: 'get',
      params: { auditStatus: 0 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(12, {
      url: '/ai/customer/statistics/risk-audits/approval-overview',
      method: 'get',
      params: { approvalStatus: 0 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(13, {
      url: '/ai/customer/statistics/risk-audits/approval-trend',
      method: 'get',
      params: { actionType: 'cancel_order' }
    })
    expect(requestMock).toHaveBeenNthCalledWith(14, {
      url: '/ai/customer/statistics/risk-audits/action-trend',
      method: 'get',
      params: { actionType: 'after_sale' }
    })
    expect(requestMock).toHaveBeenNthCalledWith(15, {
      url: '/ai/customer/statistics/risk-audits/action-approval-stats',
      method: 'get',
      params: { actionType: 'remind_order' }
    })
    expect(requestMock).toHaveBeenNthCalledWith(16, {
      url: '/ai/customer/statistics/risk-audits/export',
      method: 'get',
      params: { page: 1, pageSize: 100 },
      responseType: 'blob'
    })
    expect(requestMock).toHaveBeenNthCalledWith(17, {
      url: '/ai/customer/statistics/risk-audits',
      method: 'put',
      data: { assistantMessageId: 1, auditStatus: 1 }
    })
    expect(requestMock).toHaveBeenNthCalledWith(18, {
      url: '/ai/customer/statistics/risk-audits/approval',
      method: 'put',
      data: { assistantMessageId: 1, approvalStatus: 1 }
    })
  })

  it('report.ts 应只保留已落地的报表接口', async () => {
    const reportParams = { begin: '2026-06-01', end: '2026-06-13' }

    await getReportTurnoverStatistics(reportParams)
    await getReportUserStatistics(reportParams)
    await getReportOrderStatistics(reportParams)
    await getReportTop10(reportParams)
    await exportReport()

    expect(requestMock).toHaveBeenNthCalledWith(1, {
      url: '/report/turnoverStatistics',
      method: 'get',
      params: reportParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(2, {
      url: '/report/userStatistics',
      method: 'get',
      params: reportParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(3, {
      url: '/report/ordersStatistics',
      method: 'get',
      params: reportParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(4, {
      url: '/report/top10',
      method: 'get',
      params: reportParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(5, {
      url: '/report/export',
      method: 'get',
      responseType: 'blob'
    })
  })

  it('notification.ts 应保持当前通知中心请求封装', async () => {
    const pageParams = { pageNum: 1, pageSize: 10, status: 1 }
    const batchPayload = [1, 2, 3]

    await getNotificationPage(pageParams)
    await getUnreadNotificationCount()
    await batchReadNotification(batchPayload)
    await readNotification(99)

    expect(requestMock).toHaveBeenNthCalledWith(1, {
      url: '/messages/page',
      method: 'get',
      params: pageParams
    })
    expect(requestMock).toHaveBeenNthCalledWith(2, {
      url: '/messages/countUnread',
      method: 'get'
    })
    expect(requestMock).toHaveBeenNthCalledWith(3, {
      url: '/messages/batch',
      method: 'put',
      data: batchPayload
    })
    expect(requestMock).toHaveBeenNthCalledWith(4, {
      url: '/messages/99',
      method: 'PUT'
    })
  })
})
