<template>
  <div class="ai-operation-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">
          AI运营中心
        </h2>
        <p class="page-subtitle">
          聚合客服 AI 的知识库、会话明细、工具调用、热门问题、未命中分析和高风险审计。
        </p>
      </div>
      <el-button v-permission="'ai:operation:view'" type="primary" icon="el-icon-refresh" @click="loadAllData">
        刷新数据
      </el-button>
    </div>

    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">
          知识文档数
        </div>
        <div class="summary-value">
          {{ knowledgeList.length }}
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-label">
          FAQ文档数
        </div>
        <div class="summary-value">
          {{ faqKnowledgeList.length }}
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-label">
          会话总数
        </div>
        <div class="summary-value">
          {{ conversationList.length }}
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-label">
          工具日志数
        </div>
        <div class="summary-value">
          {{ toolLogList.length }}
        </div>
      </div>
      <div class="summary-card summary-card-approval">
        <div class="summary-label">
          审批通过率
        </div>
        <div class="summary-value">
          {{ approvalPassRateText }}
        </div>
        <div class="summary-desc">
          通过 {{ riskApprovalOverview.approvalApprovedCount }} / 总审批 {{ riskApprovalOverview.approvalTotalCount }}
        </div>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            问题热度分布
          </div>
          <el-tag size="small" type="success">
            基于热门问题 Top10
          </el-tag>
        </div>
        <div ref="hotQuestionChart" class="chart-box" />
      </div>

      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            高风险审计状态分布
          </div>
          <div class="header-actions">
            <el-radio-group v-model="riskChartMode" size="mini" @change="renderRiskAuditStatusChart">
              <el-radio-button label="audit">
                审计
              </el-radio-button>
              <el-radio-button label="approval">
                审批
              </el-radio-button>
            </el-radio-group>
            <el-tag size="small" type="danger">
              基于当前筛选条件
            </el-tag>
          </div>
        </div>
        <div ref="riskAuditStatusChart" class="chart-box" />
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-title">
          热门问题统计
        </div>
        <el-table :data="hotQuestionList" border stripe>
          <el-table-column prop="question" label="问题内容" min-width="260" show-overflow-tooltip />
          <el-table-column prop="hitCount" label="提问次数" width="120" />
        </el-table>
      </div>

      <div class="panel-card">
        <div class="panel-title">
          未命中问题分析
        </div>
        <el-table :data="fallbackQuestionList" border stripe>
          <el-table-column prop="question" label="问题内容" min-width="260" show-overflow-tooltip />
          <el-table-column prop="hitCount" label="出现次数" width="120" />
        </el-table>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card panel-card-full">
        <div class="panel-header-inline">
          <div class="panel-title">
            高风险命中与审批双轴趋势
          </div>
          <el-tag size="small" type="warning">
            按风险命中日期聚合
          </el-tag>
        </div>
        <div ref="approvalTrendChart" class="chart-box" />
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            动作类型趋势对比
          </div>
          <el-tag size="small" type="danger">
            取消订单 / 催履约 / 售后申请
          </el-tag>
        </div>
        <div ref="riskActionTrendChart" class="chart-box" />
      </div>

      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            动作类型审批通过率对比
          </div>
          <el-tag size="small" type="success">
            审批总量 + 通过率
          </el-tag>
        </div>
        <div ref="riskActionApprovalChart" class="chart-box" />
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            未命中问题趋势
          </div>
          <el-tag size="small" type="info">
            按未命中日期聚合
          </el-tag>
        </div>
        <div ref="fallbackTrendChart" class="chart-box" />
      </div>

      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            动作审批质量排行榜
          </div>
          <el-tag size="small" type="success">
            按通过率优先、审批量次序排序
          </el-tag>
        </div>
        <el-table :data="riskActionApprovalRankList" border stripe>
          <el-table-column label="排名" width="80">
            <template slot-scope="scope">
              <span class="rank-index">{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="动作类型" min-width="160">
            <template slot-scope="scope">
              <el-tag size="small" :type="getActionRankTagType(scope.row.actionType)">
                {{ formatActionType(scope.row.actionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approvalTotalCount" label="审批总量" width="120" />
          <el-table-column prop="approvalApprovedCount" label="通过数量" width="120" />
          <el-table-column label="通过率" width="120">
            <template slot-scope="scope">
              {{ formatPercent(scope.row.approvalPassRate) }}
            </template>
          </el-table-column>
          <el-table-column label="质量结论" min-width="180">
            <template slot-scope="scope">
              <span>{{ getActionApprovalQualityText(scope.row) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            知识库文档
          </div>
          <div class="header-actions">
            <el-button v-permission="'ai:knowledge:save'" type="primary" size="small" icon="el-icon-plus" @click="openKnowledgeDialog()">
              新增文档
            </el-button>
            <el-button v-permission="'ai:knowledge:save'" type="warning" size="small" icon="el-icon-edit-outline" @click="openKnowledgeDialog(undefined, 'faq')">
              新增FAQ
            </el-button>
          </div>
        </div>
        <div class="knowledge-toolbar">
          <el-input
            v-model="knowledgeKeyword"
            clearable
            size="small"
            placeholder="按标题或内容搜索知识文档"
            class="knowledge-search"
          />
          <el-radio-group v-model="knowledgeFilterType" size="small">
            <el-radio-button label="all">
              全部
            </el-radio-button>
            <el-radio-button label="faq">
              FAQ
            </el-radio-button>
            <el-radio-button label="rule">
              规则文档
            </el-radio-button>
            <el-radio-button label="after_sale">
              售后文档
            </el-radio-button>
          </el-radio-group>
        </div>
        <el-table :data="filteredKnowledgeList" border stripe>
          <el-table-column prop="title" label="文档标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="docType" label="文档类型" width="120" />
          <el-table-column prop="sourceType" label="来源类型" width="120" />
          <el-table-column prop="status" label="状态" width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已启用' : '未启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="180" />
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button v-permission="'ai:knowledge:save'" type="text" size="small" @click="openKnowledgeDialog(scope.row)">
                编辑
              </el-button>
              <el-button
                v-permission="'ai:knowledge:status'"
                type="text"
                size="small"
                @click="handleKnowledgeStatusChange(scope.row)"
              >
                {{ scope.row.status === 1 ? '停用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            FAQ管理
          </div>
          <el-tag size="small" type="warning">
            仅展示 docType=faq 的知识文档
          </el-tag>
        </div>
        <el-table :data="faqKnowledgeList" border stripe>
          <el-table-column prop="title" label="FAQ标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="content" label="FAQ答案" min-width="260" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已启用' : '未启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button v-permission="'ai:knowledge:save'" type="text" size="small" @click="openKnowledgeDialog(scope.row, 'faq')">
                编辑FAQ
              </el-button>
              <el-button v-permission="'ai:knowledge:status'" type="text" size="small" @click="handleKnowledgeStatusChange(scope.row)">
                {{ scope.row.status === 1 ? '停用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-header-inline">
          <div class="panel-title">
            高风险回答审计
          </div>
          <el-tag size="small" type="danger">
            支持处理状态与备注闭环
          </el-tag>
        </div>
        <div class="risk-audit-summary">
          <div
            v-for="item in riskAuditStatusCards"
            :key="item.key"
            class="risk-summary-card"
            :class="{ active: riskAuditStatusValue === item.value }"
            @click="handleRiskAuditQuickFilter(item.value)"
          >
            <div class="risk-summary-label">
              {{ item.label }}
            </div>
            <div class="risk-summary-value">
              {{ item.count }}
            </div>
          </div>
        </div>
        <div class="risk-audit-summary approval-summary">
          <div
            v-for="item in approvalStatusCards"
            :key="item.key"
            class="risk-summary-card"
            :class="{ active: approvalStatusValue === item.value }"
            @click="handleApprovalQuickFilter(item.value)"
          >
            <div class="risk-summary-label">
              {{ item.label }}
            </div>
            <div class="risk-summary-value">
              {{ item.count }}
            </div>
          </div>
        </div>
        <div class="risk-audit-toolbar">
          <el-input
            v-model="riskAuditQuery.conversationId"
            clearable
            size="small"
            placeholder="按会话ID筛选"
            class="risk-filter-item"
          />
          <el-input
            v-model="riskAuditQuery.userId"
            clearable
            size="small"
            placeholder="按用户ID筛选"
            class="risk-filter-item"
          />
          <el-input
            v-model="riskAuditQuery.approvalUserId"
            clearable
            size="small"
            placeholder="按审批人ID筛选"
            class="risk-filter-item"
          />
          <el-input
            v-model="riskAuditQuery.questionKeyword"
            clearable
            size="small"
            placeholder="按问题或回答关键词筛选"
            class="risk-filter-item risk-filter-keyword"
          />
          <el-select v-model="riskAuditQuery.actionType" clearable size="small" placeholder="按动作类型筛选" class="risk-filter-item">
            <el-option label="取消订单" value="cancel_order" />
            <el-option label="催履约" value="remind_order" />
            <el-option label="售后申请" value="after_sale" />
          </el-select>
          <el-select v-model="riskAuditStatusValue" clearable size="small" placeholder="按处理状态筛选" class="risk-filter-item">
            <el-option label="待处理" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已忽略" :value="2" />
          </el-select>
          <el-select v-model="approvalStatusValue" clearable size="small" placeholder="按审批状态筛选" class="risk-filter-item">
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
          <el-date-picker
            v-model="riskAuditDateRange"
            size="small"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            class="risk-filter-item risk-filter-date"
          />
          <el-date-picker
            v-model="approvalDateRange"
            size="small"
            type="datetimerange"
            range-separator="至"
            start-placeholder="审批开始时间"
            end-placeholder="审批结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            class="risk-filter-item risk-filter-date"
          />
          <el-button type="primary" size="small" @click="handleRiskAuditSearch">
            查询
          </el-button>
          <el-button v-permission="'ai:risk:export'" type="warning" size="small" @click="handleRiskAuditExport">
            导出
          </el-button>
          <el-button size="small" @click="handleRiskAuditReset">
            重置
          </el-button>
        </div>
        <el-table :data="riskAuditList" border stripe>
          <el-table-column prop="userQuestion" label="用户问题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="assistantReply" label="AI回答" min-width="260" show-overflow-tooltip />
          <el-table-column prop="actionType" label="动作类型" width="140">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.actionType" size="small" type="warning">
                {{ formatActionType(scope.row.actionType) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="riskReason" label="风险原因" min-width="160" show-overflow-tooltip />
          <el-table-column label="处理状态" width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="getRiskAuditTagType(scope.row.auditStatus)">
                {{ getRiskAuditStatusText(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="auditRemark" label="处理备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="审批状态" width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="getApprovalTagType(scope.row.approvalStatus)">
                {{ getApprovalStatusText(scope.row.approvalStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approvalRemark" label="审批备注" min-width="180" show-overflow-tooltip />
          <el-table-column prop="approvalUserId" label="审批人ID" width="120" />
          <el-table-column prop="approvalTime" label="审批时间" width="180" />
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column label="操作" width="180" fixed="right">
            <template slot-scope="scope">
              <el-button v-permission="'ai:risk:audit-handle'" type="text" size="small" @click="openRiskAuditDialog(scope.row)">
                处理
              </el-button>
              <el-button
                v-permission="'ai:risk:approve'"
                type="text"
                size="small"
                :disabled="!canApprove(scope.row)"
                @click="openRiskApprovalDialog(scope.row)"
              >
                审批
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="risk-audit-pagination">
          <el-pagination
            background
            layout="total, prev, pager, next, sizes"
            :total="riskAuditTotal"
            :current-page="riskAuditQuery.page"
            :page-size="riskAuditQuery.pageSize"
            :page-sizes="[10, 20, 50]"
            @current-change="handleRiskAuditPageChange"
            @size-change="handleRiskAuditSizeChange"
          />
        </div>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <div class="panel-title">
          工具调用日志
        </div>
        <el-table :data="toolLogList" border stripe>
          <el-table-column prop="toolName" label="工具名称" width="160" />
          <el-table-column prop="success" label="执行结果" width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="scope.row.success ? 'success' : 'danger'">
                {{ scope.row.success ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestBody" label="请求参数" min-width="220" show-overflow-tooltip />
          <el-table-column prop="errorMessage" label="失败原因" min-width="180" show-overflow-tooltip />
          <el-table-column prop="createTime" label="时间" width="180" />
        </el-table>
      </div>

      <div class="panel-card">
        <div class="panel-title">
          客服会话列表
        </div>
        <el-table :data="conversationList" border stripe @row-click="handleConversationClick">
          <el-table-column prop="conversationId" label="会话ID" width="110" />
          <el-table-column prop="title" label="会话标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="lastMessage" label="最后消息" min-width="220" show-overflow-tooltip />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
        </el-table>
      </div>
    </div>

    <div class="panel-card conversation-panel">
      <div class="panel-title">
        会话消息明细
        <span v-if="activeConversationId" class="panel-extra">当前会话：{{ activeConversationId }}</span>
      </div>
      <el-empty v-if="messageList.length === 0" description="点击右侧会话后查看消息明细" />
      <el-table v-else :data="messageList" border stripe>
        <el-table-column prop="role" label="角色" width="120">
          <template slot-scope="scope">
            <el-tag size="small" :type="scope.row.role === 'assistant' ? 'success' : 'warning'">
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="消息内容" min-width="360" show-overflow-tooltip />
        <el-table-column prop="toolName" label="路由/工具" width="140" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </div>

    <el-dialog
      :title="knowledgeForm.id ? '编辑知识文档' : '新增知识文档'"
      :visible.sync="knowledgeDialogVisible"
      width="760px"
      :close-on-click-modal="false"
    >
      <el-form ref="knowledgeFormRef" :model="knowledgeForm" :rules="knowledgeRules" label-width="100px">
        <el-form-item label="文档标题" prop="title">
          <el-input v-model="knowledgeForm.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="文档类型" prop="docType">
          <el-select v-model="knowledgeForm.docType" placeholder="请选择文档类型" style="width: 100%;">
            <el-option label="FAQ" value="faq" />
            <el-option label="规则文档" value="rule" />
            <el-option label="售后文档" value="after_sale" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源类型" prop="sourceType">
          <el-select v-model="knowledgeForm.sourceType" placeholder="请选择来源类型" style="width: 100%;">
            <el-option label="手工录入" value="manual" />
            <el-option label="后台维护" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态" prop="status">
          <el-switch
            v-model="knowledgeForm.statusSwitch"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
        <el-form-item label="文档内容" prop="content">
          <el-input
            v-model="knowledgeForm.content"
            type="textarea"
            :rows="10"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="knowledgeDialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" :loading="knowledgeSubmitting" @click="submitKnowledgeForm">
          保存
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="处理高风险回答"
      :visible.sync="riskAuditDialogVisible"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form ref="riskAuditFormRef" :model="riskAuditForm" :rules="riskAuditRules" label-width="100px">
        <el-form-item label="用户问题">
          <el-input :value="riskAuditForm.userQuestion" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="AI回答">
          <el-input :value="riskAuditForm.assistantReply" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="处理结果" prop="auditStatus">
          <el-radio-group v-model="riskAuditForm.auditStatus">
            <el-radio :label="1">
              已确认
            </el-radio>
            <el-radio :label="2">
              已忽略
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注" prop="auditRemark">
          <el-input
            v-model="riskAuditForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="请输入处理说明，便于后续追踪"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="riskAuditDialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" :loading="riskAuditSubmitting" @click="submitRiskAuditForm">
          保存
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="审批高风险动作"
      :visible.sync="riskApprovalDialogVisible"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form ref="riskApprovalFormRef" :model="riskApprovalForm" :rules="riskApprovalRules" label-width="100px">
        <el-form-item label="用户问题">
          <el-input :value="riskApprovalForm.userQuestion" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="AI回答">
          <el-input :value="riskApprovalForm.assistantReply" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalStatus">
          <el-radio-group v-model="riskApprovalForm.approvalStatus">
            <el-radio :label="1">
              已通过
            </el-radio>
            <el-radio :label="2">
              已驳回
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注" prop="approvalRemark">
          <el-input
            v-model="riskApprovalForm.approvalRemark"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="请输入审批说明，便于后续追踪"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="riskApprovalDialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" :loading="riskApprovalSubmitting" @click="submitRiskApprovalForm">
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import * as echarts from 'echarts'
import {
  getAiConversationList,
  getAiMessageList,
  getAiKnowledgeList,
  saveAiKnowledge,
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
  updateAiKnowledgeStatus,
  updateAiRiskAudit,
  updateAiRiskApproval
} from '@/api/ai-operation'

@Component({
  name: 'AiOperation'
})
export default class extends Vue {
  private loading = false
  private activeConversationId = ''
  private hotQuestionChart: any = null
  private riskAuditStatusChart: any = null
  private approvalTrendChart: any = null
  private riskActionTrendChart: any = null
  private fallbackTrendChart: any = null
  private riskActionApprovalChart: any = null
  private riskChartMode = 'audit'
  private knowledgeList = [] as any[]
  private conversationList = [] as any[]
  private messageList = [] as any[]
  private toolLogList = [] as any[]
  private hotQuestionList = [] as any[]
  private fallbackQuestionList = [] as any[]
  private riskAuditList = [] as any[]
  private riskAuditTotal = 0
  private riskAuditSummary = {
    totalCount: 0,
    pendingCount: 0,
    confirmedCount: 0,
    ignoredCount: 0,
    approvalPendingCount: 0,
    approvalApprovedCount: 0,
    approvalRejectedCount: 0
  }
  private riskApprovalOverview = {
    approvalTotalCount: 0,
    approvalApprovedCount: 0,
    approvalRejectedCount: 0,
    approvalPassRate: 0
  }
  private riskApprovalTrendList = [] as any[]
  private riskActionTrendList = [] as any[]
  private fallbackTrendList = [] as any[]
  private riskActionApprovalStats = [] as any[]
  private knowledgeDialogVisible = false
  private knowledgeSubmitting = false
  private knowledgeKeyword = ''
  private knowledgeFilterType = 'all'
  private knowledgeForm = this.createKnowledgeForm()
  private riskAuditDialogVisible = false
  private riskAuditSubmitting = false
  private riskAuditForm = this.createRiskAuditForm()
  private riskApprovalDialogVisible = false
  private riskApprovalSubmitting = false
  private riskApprovalForm = this.createRiskApprovalForm()
  private riskAuditQuery = {
    page: 1,
    pageSize: 10,
    conversationId: '',
    userId: '',
    approvalUserId: '',
    actionType: '',
    auditStatus: 0,
    approvalStatus: 0,
    questionKeyword: '',
    beginTime: '',
    endTime: '',
    approvalBeginTime: '',
    approvalEndTime: ''
  }
  private riskAuditDateRange = [] as string[]
  private approvalDateRange = [] as string[]
  private knowledgeRules = {
    title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }],
    docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }],
    sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
    content: [{ required: true, message: '请输入文档内容', trigger: 'blur' }]
  }
  private riskAuditRules = {
    auditStatus: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
    auditRemark: [{ required: true, message: '请输入处理备注', trigger: 'blur' }]
  }
  private riskApprovalRules = {
    approvalStatus: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
    approvalRemark: [{ required: true, message: '请输入审批备注', trigger: 'blur' }]
  }

  created() {
    this.loadAllData()
  }

  mounted() {
    window.addEventListener('resize', this.handleChartResize)
  }

  beforeDestroy() {
    window.removeEventListener('resize', this.handleChartResize)
    this.disposeCharts()
  }

  // 统一加载 AI 运营页需要的核心数据。
  async loadAllData() {
    this.loading = true
    try {
      const [
        knowledgeRes,
        conversationRes,
        toolLogRes,
        hotRes,
        fallbackRes,
        riskRes,
        riskSummaryRes,
        approvalOverviewRes,
        approvalTrendRes,
        riskActionTrendRes,
        fallbackTrendRes,
        riskActionApprovalStatsRes
      ] = await Promise.all([
        getAiKnowledgeList(),
        getAiConversationList(),
        getAiToolLogList(),
        getAiHotQuestionList(10),
        getAiFallbackQuestionList(10),
        getAiRiskAuditPage(this.riskAuditQuery),
        getAiRiskAuditSummary(this.buildRiskAuditSummaryQuery()),
        getAiRiskApprovalOverview(this.buildRiskAuditSummaryQuery()),
        getAiRiskApprovalTrend(this.buildRiskAuditSummaryQuery()),
        getAiRiskActionTrend(this.buildRiskAuditSummaryQuery()),
        getAiFallbackTrend(10),
        getAiRiskActionApprovalStats(this.buildRiskAuditSummaryQuery())
      ])

      this.knowledgeList = this.resolveList(knowledgeRes)
      this.conversationList = this.resolveList(conversationRes)
      this.toolLogList = this.resolveList(toolLogRes)
      this.hotQuestionList = this.resolveList(hotRes)
      this.fallbackQuestionList = this.resolveList(fallbackRes)
      this.resolveRiskAuditPage(riskRes)
      this.resolveRiskAuditSummary(riskSummaryRes)
      this.resolveRiskApprovalOverview(approvalOverviewRes)
      this.riskApprovalTrendList = this.resolveList(approvalTrendRes)
      this.riskActionTrendList = this.resolveList(riskActionTrendRes)
      this.fallbackTrendList = this.resolveList(fallbackTrendRes).reverse()
      this.riskActionApprovalStats = this.resolveList(riskActionApprovalStatsRes)
      this.$nextTick(() => {
        this.renderHotQuestionChart()
        this.renderRiskAuditStatusChart()
        this.renderApprovalTrendChart()
        this.renderRiskActionTrendChart()
        this.renderRiskActionApprovalChart()
        this.renderFallbackTrendChart()
      })

      if (this.conversationList.length > 0) {
        this.handleConversationClick(this.conversationList[0])
      } else {
        this.messageList = []
        this.activeConversationId = ''
      }
    } catch (error) {
      this.$message.error('AI运营数据加载失败')
    } finally {
      this.loading = false
    }
  }

  // 点击会话后加载消息明细。
  async handleConversationClick(row: any) {
    if (!row || !row.conversationId) {
      return
    }
    this.activeConversationId = String(row.conversationId)
    try {
      const res = await getAiMessageList(row.conversationId)
      this.messageList = this.resolveList(res)
    } catch (error) {
      this.messageList = []
      this.$message.error('会话消息加载失败')
    }
  }

  // 打开知识库新增或编辑弹窗。
  openKnowledgeDialog(row?: any, forceDocType?: string) {
    if (row) {
      this.knowledgeForm = {
        id: row.id,
        title: row.title || '',
        docType: forceDocType || row.docType || 'faq',
        sourceType: row.sourceType || 'manual',
        sourceId: row.sourceId || '',
        content: row.content || '',
        status: typeof row.status === 'number' ? row.status : 1,
        statusSwitch: row.status === 1
      }
    } else {
      this.knowledgeForm = this.createKnowledgeForm()
      if (forceDocType) {
        this.knowledgeForm.docType = forceDocType
      }
    }
    this.knowledgeDialogVisible = true
    this.$nextTick(() => {
      const formRef = this.$refs.knowledgeFormRef as any
      formRef && formRef.clearValidate()
    })
  }

  // 提交知识库文档，保存后刷新列表。
  submitKnowledgeForm() {
    const formRef = this.$refs.knowledgeFormRef as any
    if (!formRef) {
      return
    }
    formRef.validate(async (valid: boolean) => {
      if (!valid) {
        return
      }
      this.knowledgeSubmitting = true
      try {
        const payload = {
          id: this.knowledgeForm.id || undefined,
          title: this.knowledgeForm.title,
          docType: this.knowledgeForm.docType,
          sourceType: this.knowledgeForm.sourceType,
          sourceId: this.knowledgeForm.sourceId || undefined,
          content: this.knowledgeForm.content,
          status: this.knowledgeForm.statusSwitch ? 1 : 0
        }
        const res = await saveAiKnowledge(payload)
        if (res && res.data && res.data.code === 1) {
          this.$message.success('知识文档保存成功')
          this.knowledgeDialogVisible = false
          await this.loadKnowledgeList()
        } else {
          this.$message.error('知识文档保存失败')
        }
      } catch (error) {
        this.$message.error('知识文档保存失败')
      } finally {
        this.knowledgeSubmitting = false
      }
    })
  }

  // 切换知识文档状态。
  async handleKnowledgeStatusChange(row: any) {
    if (!row || !row.id) {
      return
    }
    const nextStatus = row.status === 1 ? 0 : 1
    const actionText = nextStatus === 1 ? '启用' : '停用'
    try {
      await this.$confirm(`确认${actionText}当前知识文档吗？`, '提示', {
        type: 'warning'
      })
      const res = await updateAiKnowledgeStatus(row.id, nextStatus)
      if (res && res.data && res.data.code === 1) {
        this.$message.success(`知识文档已${actionText}`)
        await this.loadKnowledgeList()
      } else {
        this.$message.error(`${actionText}失败`)
      }
    } catch (error) {
      // 用户取消操作时不提示错误。
    }
  }

  // 打开高风险回答处理弹窗。
  openRiskAuditDialog(row: any) {
    this.riskAuditForm = {
      assistantMessageId: row.assistantMessageId,
      userQuestion: row.userQuestion || '',
      assistantReply: row.assistantReply || '',
      auditStatus: row.auditStatus && row.auditStatus > 0 ? row.auditStatus : 1,
      auditRemark: row.auditRemark || ''
    }
    this.riskAuditDialogVisible = true
    this.$nextTick(() => {
      const formRef = this.$refs.riskAuditFormRef as any
      formRef && formRef.clearValidate()
    })
  }

  // 打开高风险动作审批弹窗。
  openRiskApprovalDialog(row: any) {
    if (!this.canApprove(row)) {
      this.$message.warning('请先完成高风险审计处理，再进行审批')
      return
    }
    this.riskApprovalForm = {
      assistantMessageId: row.assistantMessageId,
      userQuestion: row.userQuestion || '',
      assistantReply: row.assistantReply || '',
      approvalStatus: row.approvalStatus && row.approvalStatus > 0 ? row.approvalStatus : 1,
      approvalRemark: row.approvalRemark || ''
    }
    this.riskApprovalDialogVisible = true
    this.$nextTick(() => {
      const formRef = this.$refs.riskApprovalFormRef as any
      formRef && formRef.clearValidate()
    })
  }

  // 提交高风险回答处理结果。
  submitRiskAuditForm() {
    const formRef = this.$refs.riskAuditFormRef as any
    if (!formRef) {
      return
    }
    formRef.validate(async (valid: boolean) => {
      if (!valid) {
        return
      }
      this.riskAuditSubmitting = true
      try {
        const payload = {
          assistantMessageId: this.riskAuditForm.assistantMessageId,
          auditStatus: this.riskAuditForm.auditStatus,
          auditRemark: this.riskAuditForm.auditRemark
        }
        const res = await updateAiRiskAudit(payload)
        if (res && res.data && res.data.code === 1) {
          this.$message.success('高风险审计处理成功')
          this.riskAuditDialogVisible = false
          await this.loadRiskAuditList()
        } else {
          this.$message.error('高风险审计处理失败')
        }
      } catch (error) {
        this.$message.error('高风险审计处理失败')
      } finally {
        this.riskAuditSubmitting = false
      }
    })
  }

  // 提交高风险动作审批结果。
  submitRiskApprovalForm() {
    const formRef = this.$refs.riskApprovalFormRef as any
    if (!formRef) {
      return
    }
    formRef.validate(async (valid: boolean) => {
      if (!valid) {
        return
      }
      this.riskApprovalSubmitting = true
      try {
        const payload = {
          assistantMessageId: this.riskApprovalForm.assistantMessageId,
          approvalStatus: this.riskApprovalForm.approvalStatus,
          approvalRemark: this.riskApprovalForm.approvalRemark
        }
        const res = await updateAiRiskApproval(payload)
        if (res && res.data && res.data.code === 1) {
          this.$message.success('高风险动作审批成功')
          this.riskApprovalDialogVisible = false
          await this.loadRiskAuditList()
        } else {
          this.$message.error('高风险动作审批失败')
        }
      } catch (error) {
        this.$message.error('高风险动作审批失败')
      } finally {
        this.riskApprovalSubmitting = false
      }
    })
  }

  // 单独刷新知识库列表，便于增删改后复用。
  private async loadKnowledgeList() {
    const knowledgeRes = await getAiKnowledgeList()
    this.knowledgeList = this.resolveList(knowledgeRes)
  }

  // 单独刷新高风险审计列表。
  private async loadRiskAuditList() {
    const [riskRes, riskSummaryRes, approvalOverviewRes, approvalTrendRes, riskActionTrendRes, riskActionApprovalStatsRes] = await Promise.all([
      getAiRiskAuditPage(this.riskAuditQuery),
      getAiRiskAuditSummary(this.buildRiskAuditSummaryQuery()),
      getAiRiskApprovalOverview(this.buildRiskAuditSummaryQuery()),
      getAiRiskApprovalTrend(this.buildRiskAuditSummaryQuery()),
      getAiRiskActionTrend(this.buildRiskAuditSummaryQuery()),
      getAiRiskActionApprovalStats(this.buildRiskAuditSummaryQuery())
    ])
    this.resolveRiskAuditPage(riskRes)
    this.resolveRiskAuditSummary(riskSummaryRes)
    this.resolveRiskApprovalOverview(approvalOverviewRes)
    this.riskApprovalTrendList = this.resolveList(approvalTrendRes)
    this.riskActionTrendList = this.resolveList(riskActionTrendRes)
    this.riskActionApprovalStats = this.resolveList(riskActionApprovalStatsRes)
    this.$nextTick(() => {
      this.renderRiskAuditStatusChart()
      this.renderApprovalTrendChart()
      this.renderRiskActionTrendChart()
      this.renderRiskActionApprovalChart()
    })
  }

  // 兼容后端统一 Result 返回结构。
  private resolveList(response: any) {
    return response && response.data && Array.isArray(response.data.data)
      ? response.data.data
      : []
  }

  // 兼容分页返回结构，提取审计列表和总数。
  private resolveRiskAuditPage(response: any) {
    const pageData = response && response.data ? response.data.data : null
    this.riskAuditList = pageData && Array.isArray(pageData.records) ? pageData.records : []
    this.riskAuditTotal = pageData && typeof pageData.total === 'number' ? pageData.total : 0
  }

  // 兼容统计返回结构，提取待处理、已确认、已忽略数量。
  private resolveRiskAuditSummary(response: any) {
    const summary = response && response.data ? response.data.data : null
    this.riskAuditSummary = {
      totalCount: summary && typeof summary.totalCount === 'number' ? summary.totalCount : 0,
      pendingCount: summary && typeof summary.pendingCount === 'number' ? summary.pendingCount : 0,
      confirmedCount: summary && typeof summary.confirmedCount === 'number' ? summary.confirmedCount : 0,
      ignoredCount: summary && typeof summary.ignoredCount === 'number' ? summary.ignoredCount : 0,
      approvalPendingCount: summary && typeof summary.approvalPendingCount === 'number' ? summary.approvalPendingCount : 0,
      approvalApprovedCount: summary && typeof summary.approvalApprovedCount === 'number' ? summary.approvalApprovedCount : 0,
      approvalRejectedCount: summary && typeof summary.approvalRejectedCount === 'number' ? summary.approvalRejectedCount : 0
    }
  }

  // 兼容审批概览返回结构，提取审批总量、通过量和通过率。
  private resolveRiskApprovalOverview(response: any) {
    const overview = response && response.data ? response.data.data : null
    this.riskApprovalOverview = {
      approvalTotalCount: overview && typeof overview.approvalTotalCount === 'number' ? overview.approvalTotalCount : 0,
      approvalApprovedCount: overview && typeof overview.approvalApprovedCount === 'number' ? overview.approvalApprovedCount : 0,
      approvalRejectedCount: overview && typeof overview.approvalRejectedCount === 'number' ? overview.approvalRejectedCount : 0,
      approvalPassRate: overview && typeof overview.approvalPassRate === 'number' ? overview.approvalPassRate : 0
    }
  }

  // 基于当前筛选条件返回知识文档列表。
  get filteredKnowledgeList() {
    const keyword = (this.knowledgeKeyword || '').trim().toLowerCase()
    return this.knowledgeList.filter((item: any) => {
      const matchType = this.knowledgeFilterType === 'all' || item.docType === this.knowledgeFilterType
      const matchKeyword = !keyword ||
        String(item.title || '').toLowerCase().includes(keyword) ||
        String(item.content || '').toLowerCase().includes(keyword)
      return matchType && matchKeyword
    })
  }

  // FAQ 视图，只保留 FAQ 类型文档。
  get faqKnowledgeList() {
    return this.knowledgeList.filter((item: any) => item.docType === 'faq')
  }

  // 风险审计快捷状态卡片。
  get riskAuditStatusCards() {
    return [
      { key: 'pending', label: '待处理', value: 0, count: this.riskAuditSummary.pendingCount },
      { key: 'confirmed', label: '已确认', value: 1, count: this.riskAuditSummary.confirmedCount },
      { key: 'ignored', label: '已忽略', value: 2, count: this.riskAuditSummary.ignoredCount },
      { key: 'all', label: '全部审计', value: '', count: this.riskAuditSummary.totalCount }
    ]
  }

  // 审批快捷状态卡片。
  get approvalStatusCards() {
    return [
      { key: 'approval-pending', label: '待审批', value: 0, count: this.riskAuditSummary.approvalPendingCount },
      { key: 'approval-approved', label: '已通过', value: 1, count: this.riskAuditSummary.approvalApprovedCount },
      { key: 'approval-rejected', label: '已驳回', value: 2, count: this.riskAuditSummary.approvalRejectedCount },
      { key: 'approval-all', label: '全部审批', value: '', count: this.riskAuditSummary.totalCount }
    ]
  }

  // 审批通过率展示文案。
  get approvalPassRateText() {
    return `${Number(this.riskApprovalOverview.approvalPassRate || 0).toFixed(2)}%`
  }

  // 动作审批质量排行榜，按通过率优先、审批量次序排序，便于运营横向比较。
  get riskActionApprovalRankList() {
    return [...this.riskActionApprovalStats].sort((first: any, second: any) => {
      const firstRate = Number(first && first.approvalPassRate ? first.approvalPassRate : 0)
      const secondRate = Number(second && second.approvalPassRate ? second.approvalPassRate : 0)
      if (secondRate !== firstRate) {
        return secondRate - firstRate
      }
      const firstTotal = Number(first && first.approvalTotalCount ? first.approvalTotalCount : 0)
      const secondTotal = Number(second && second.approvalTotalCount ? second.approvalTotalCount : 0)
      return secondTotal - firstTotal
    })
  }

  // 兼容全部筛选场景，统一处理状态查询值。
  get riskAuditStatusValue() {
    return this.riskAuditQuery.auditStatus === undefined ? '' : this.riskAuditQuery.auditStatus
  }

  set riskAuditStatusValue(value: number | string) {
    this.riskAuditQuery.auditStatus = value === '' ? undefined : Number(value)
  }

  // 兼容全部筛选场景，统一审批状态查询值。
  get approvalStatusValue() {
    return this.riskAuditQuery.approvalStatus === undefined ? '' : this.riskAuditQuery.approvalStatus
  }

  set approvalStatusValue(value: number | string) {
    this.riskAuditQuery.approvalStatus = value === '' ? undefined : Number(value)
  }

  // 风险审计状态展示文案。
  getRiskAuditStatusText(status: number) {
    switch (status) {
      case 1:
        return '已确认'
      case 2:
        return '已忽略'
      default:
        return '待处理'
    }
  }

  // 风险审计状态标签颜色。
  getRiskAuditTagType(status: number) {
    switch (status) {
      case 1:
        return 'success'
      case 2:
        return 'info'
      default:
        return 'danger'
    }
  }

  // 审批状态展示文案。
  getApprovalStatusText(status: number) {
    switch (status) {
      case 1:
        return '已通过'
      case 2:
        return '已驳回'
      default:
        return '待审批'
    }
  }

  // 审批状态标签颜色。
  getApprovalTagType(status: number) {
    switch (status) {
      case 1:
        return 'success'
      case 2:
        return 'danger'
      default:
        return 'warning'
    }
  }

  // 仅允许已完成审计处理的记录进入审批环节。
  canApprove(row: any) {
    return !!row && row.auditStatus !== undefined && row.auditStatus !== null && Number(row.auditStatus) > 0
  }

  // 将动作类型转换为便于运营理解的中文文案。
  formatActionType(actionType: string) {
    switch (actionType) {
      case 'cancel_order':
        return '取消订单'
      case 'remind_order':
        return '催履约'
      case 'after_sale':
        return '售后申请'
      default:
        return actionType || '-'
    }
  }

  // 统一格式化百分比，保证表格和卡片展示口径一致。
  formatPercent(value: number) {
    return `${Number(value || 0).toFixed(2)}%`
  }

  // 为不同动作类型返回差异化标签颜色，提升排行榜扫描效率。
  getActionRankTagType(actionType: string) {
    switch (actionType) {
      case 'cancel_order':
        return 'danger'
      case 'remind_order':
        return 'warning'
      case 'after_sale':
        return 'primary'
      default:
        return 'info'
    }
  }

  // 输出运营可直接理解的质量结论，减少人工二次判断成本。
  getActionApprovalQualityText(row: any) {
    const approvalTotalCount = Number(row && row.approvalTotalCount ? row.approvalTotalCount : 0)
    const approvalPassRate = Number(row && row.approvalPassRate ? row.approvalPassRate : 0)
    if (approvalTotalCount <= 0) {
      return '暂无已审批样本'
    }
    if (approvalPassRate >= 80) {
      return '审批质量高，建议保持当前策略'
    }
    if (approvalPassRate >= 50) {
      return '审批质量中等，建议继续观察'
    }
    return '审批质量偏低，建议优先复盘规则'
  }

  // 查询高风险审计列表。
  handleRiskAuditSearch() {
    this.riskAuditQuery.page = 1
    this.syncRiskAuditDateRange()
    this.loadRiskAuditList()
  }

  // 通过状态卡片快速切换待处理、已确认、已忽略和全部视图。
  handleRiskAuditQuickFilter(status: number | string) {
    this.riskAuditStatusValue = status
    this.handleRiskAuditSearch()
  }

  // 通过状态卡片快速切换待审批、已通过、已驳回和全部视图。
  handleApprovalQuickFilter(status: number | string) {
    this.approvalStatusValue = status
    this.handleRiskAuditSearch()
  }

  // 重置高风险审计筛选条件。
  handleRiskAuditReset() {
    this.riskAuditQuery = {
      page: 1,
      pageSize: 10,
      conversationId: '',
      userId: '',
      approvalUserId: '',
      actionType: '',
      auditStatus: 0,
      approvalStatus: 0,
      questionKeyword: '',
      beginTime: '',
      endTime: '',
      approvalBeginTime: '',
      approvalEndTime: ''
    }
    this.riskAuditDateRange = []
    this.approvalDateRange = []
    this.loadRiskAuditList()
  }

  // 切换高风险审计页码。
  handleRiskAuditPageChange(page: number) {
    this.riskAuditQuery.page = page
    this.syncRiskAuditDateRange()
    this.loadRiskAuditList()
  }

  // 切换高风险审计分页大小。
  handleRiskAuditSizeChange(pageSize: number) {
    this.riskAuditQuery.pageSize = pageSize
    this.riskAuditQuery.page = 1
    this.syncRiskAuditDateRange()
    this.loadRiskAuditList()
  }

  // 导出当前筛选条件下的高风险审计数据。
  async handleRiskAuditExport() {
    this.syncRiskAuditDateRange()
    try {
      await this.$confirm('是否确认导出当前筛选条件下的高风险审计数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const queryParams = {
        ...this.riskAuditQuery,
        page: 1,
        pageSize: 1000
      }
      const { data } = await exportAiRiskAudits(queryParams)
      const url = window.URL.createObjectURL(data)
      const link = document.createElement('a')
      document.body.appendChild(link)
      link.href = url
      link.download = '客服AI高风险审计报表.xlsx'
      link.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(link)
    } catch (error) {
      // 用户取消导出时不提示错误。
    }
  }

  // 同步时间范围到查询参数，避免接口接收到无效值。
  private syncRiskAuditDateRange() {
    this.riskAuditQuery.beginTime = this.riskAuditDateRange && this.riskAuditDateRange.length > 0 ? this.riskAuditDateRange[0] : ''
    this.riskAuditQuery.endTime = this.riskAuditDateRange && this.riskAuditDateRange.length > 1 ? this.riskAuditDateRange[1] : ''
    this.riskAuditQuery.approvalBeginTime = this.approvalDateRange && this.approvalDateRange.length > 0 ? this.approvalDateRange[0] : ''
    this.riskAuditQuery.approvalEndTime = this.approvalDateRange && this.approvalDateRange.length > 1 ? this.approvalDateRange[1] : ''
  }

  // 构建审计统计查询参数，避免把分页字段带给统计接口。
  private buildRiskAuditSummaryQuery() {
    return {
      conversationId: this.riskAuditQuery.conversationId || undefined,
      userId: this.riskAuditQuery.userId || undefined,
      approvalUserId: this.riskAuditQuery.approvalUserId || undefined,
      actionType: this.riskAuditQuery.actionType || undefined,
      approvalStatus: this.riskAuditQuery.approvalStatus,
      questionKeyword: this.riskAuditQuery.questionKeyword || undefined,
      beginTime: this.riskAuditQuery.beginTime || undefined,
      endTime: this.riskAuditQuery.endTime || undefined,
      approvalBeginTime: this.riskAuditQuery.approvalBeginTime || undefined,
      approvalEndTime: this.riskAuditQuery.approvalEndTime || undefined
    }
  }

  // 渲染热门问题柱状图，便于运营快速识别高频咨询主题。
  private renderHotQuestionChart() {
    const chartDom = this.$refs.hotQuestionChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.hotQuestionChart) {
      this.hotQuestionChart = echarts.init(chartDom)
    }
    const questionList = this.hotQuestionList.slice(0, 10)
    this.hotQuestionChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        top: 20,
        left: 20,
        right: 20,
        bottom: 20,
        containLabel: true
      },
      xAxis: {
        type: 'value',
        minInterval: 1,
        axisLine: {
          lineStyle: {
            color: '#dcdfe6'
          }
        }
      },
      yAxis: {
        type: 'category',
        axisTick: {
          show: false
        },
        axisLine: {
          show: false
        },
        axisLabel: {
          color: '#606266',
          width: 180,
          overflow: 'truncate'
        },
        data: questionList.map((item: any) => item.question || '-')
      },
      series: [
        {
          name: '提问次数',
          type: 'bar',
          barWidth: 16,
          data: questionList.map((item: any) => item.hitCount || 0),
          itemStyle: {
            color: '#409EFF',
            borderRadius: [0, 8, 8, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#606266'
          }
        }
      ]
    })
  }

  // 渲染高风险审计状态图，便于运营快速评估待处理积压。
  private renderRiskAuditStatusChart() {
    const chartDom = this.$refs.riskAuditStatusChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.riskAuditStatusChart) {
      this.riskAuditStatusChart = echarts.init(chartDom)
    }
    this.riskAuditStatusChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        bottom: 0,
        icon: 'circle'
      },
      title: {
        text: this.riskChartMode === 'approval' ? '审批分布' : '审计分布',
        left: 'center',
        top: '38%',
        textStyle: {
          fontSize: 14,
          fontWeight: 400,
          color: '#909399'
        }
      },
      series: [
        {
          name: this.riskChartMode === 'approval' ? '审批状态' : '审计状态',
          type: 'pie',
          radius: ['46%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            formatter: '{b}\n{c}'
          },
          labelLine: {
            length: 10,
            length2: 8
          },
          data: this.riskChartMode === 'approval'
            ? [
                { value: this.riskAuditSummary.approvalPendingCount, name: '待审批', itemStyle: { color: '#E6A23C' } },
                { value: this.riskAuditSummary.approvalApprovedCount, name: '已通过', itemStyle: { color: '#67C23A' } },
                { value: this.riskAuditSummary.approvalRejectedCount, name: '已驳回', itemStyle: { color: '#F56C6C' } }
              ]
            : [
                { value: this.riskAuditSummary.pendingCount, name: '待处理', itemStyle: { color: '#F56C6C' } },
                { value: this.riskAuditSummary.confirmedCount, name: '已确认', itemStyle: { color: '#67C23A' } },
                { value: this.riskAuditSummary.ignoredCount, name: '已忽略', itemStyle: { color: '#909399' } }
              ]
        }
      ]
    })
  }

  // 渲染审批趋势图，便于运营观察审批通过与驳回变化。
  private renderApprovalTrendChart() {
    const chartDom = this.$refs.approvalTrendChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.approvalTrendChart) {
      this.approvalTrendChart = echarts.init(chartDom)
    }
    const trendList = this.riskApprovalTrendList || []
    this.approvalTrendChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        top: 0
      },
      grid: {
        top: 40,
        left: 20,
        right: 20,
        bottom: 20,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendList.map((item: any) => item.statDate || '-')
      },
      yAxis: [
        {
          type: 'value',
          minInterval: 1,
          name: '数量'
        },
        {
          type: 'value',
          min: 0,
          max: 100,
          name: '通过率%',
          axisLabel: {
            formatter: '{value}%'
          }
        }
      ],
      series: [
        {
          name: '高风险命中',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.riskHitCount || 0),
          itemStyle: {
            color: '#409EFF'
          }
        },
        {
          name: '已通过',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.approvalApprovedCount || 0),
          itemStyle: {
            color: '#67C23A'
          }
        },
        {
          name: '已驳回',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.approvalRejectedCount || 0),
          itemStyle: {
            color: '#F56C6C'
          }
        },
        {
          name: '通过率',
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: trendList.map((item: any) => item.approvalPassRate || 0),
          itemStyle: {
            color: '#E6A23C'
          },
          lineStyle: {
            type: 'dashed'
          }
        }
      ]
    })
  }

  // 渲染动作类型趋势图，便于对比不同高风险动作的波动情况。
  private renderRiskActionTrendChart() {
    const chartDom = this.$refs.riskActionTrendChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.riskActionTrendChart) {
      this.riskActionTrendChart = echarts.init(chartDom)
    }
    const trendList = this.riskActionTrendList || []
    this.riskActionTrendChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        top: 0
      },
      grid: {
        top: 40,
        left: 20,
        right: 20,
        bottom: 20,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendList.map((item: any) => item.statDate || '-')
      },
      yAxis: {
        type: 'value',
        minInterval: 1
      },
      series: [
        {
          name: '取消订单',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.cancelOrderCount || 0),
          itemStyle: {
            color: '#F56C6C'
          }
        },
        {
          name: '催履约',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.remindOrderCount || 0),
          itemStyle: {
            color: '#E6A23C'
          }
        },
        {
          name: '售后申请',
          type: 'line',
          smooth: true,
          data: trendList.map((item: any) => item.afterSaleCount || 0),
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    })
  }

  // 渲染动作类型审批通过率对比图，便于横向比较不同动作的审批质量。
  private renderRiskActionApprovalChart() {
    const chartDom = this.$refs.riskActionApprovalChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.riskActionApprovalChart) {
      this.riskActionApprovalChart = echarts.init(chartDom)
    }
    const statsList = this.riskActionApprovalStats || []
    this.riskActionApprovalChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        top: 0
      },
      grid: {
        top: 40,
        left: 20,
        right: 20,
        bottom: 20,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: statsList.map((item: any) => this.formatActionType(item.actionType))
      },
      yAxis: [
        {
          type: 'value',
          minInterval: 1,
          name: '审批量'
        },
        {
          type: 'value',
          min: 0,
          max: 100,
          name: '通过率%',
          axisLabel: {
            formatter: '{value}%'
          }
        }
      ],
      series: [
        {
          name: '审批总量',
          type: 'bar',
          barWidth: 24,
          data: statsList.map((item: any) => item.approvalTotalCount || 0),
          itemStyle: {
            color: '#409EFF',
            borderRadius: [6, 6, 0, 0]
          }
        },
        {
          name: '通过率',
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: statsList.map((item: any) => item.approvalPassRate || 0),
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    })
  }

  // 渲染未命中问题趋势图，便于观察知识库缺口变化。
  private renderFallbackTrendChart() {
    const chartDom = this.$refs.fallbackTrendChart as HTMLDivElement
    if (!chartDom) {
      return
    }
    if (!this.fallbackTrendChart) {
      this.fallbackTrendChart = echarts.init(chartDom)
    }
    const trendList = this.fallbackTrendList || []
    this.fallbackTrendChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        top: 20,
        left: 20,
        right: 20,
        bottom: 20,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendList.map((item: any) => item.statDate || '-')
      },
      yAxis: {
        type: 'value',
        minInterval: 1
      },
      series: [
        {
          name: '未命中次数',
          type: 'line',
          smooth: true,
          areaStyle: {
            color: 'rgba(144, 147, 153, 0.15)'
          },
          data: trendList.map((item: any) => item.fallbackCount || 0),
          itemStyle: {
            color: '#909399'
          }
        }
      ]
    })
  }

  // 统一处理图表自适应，避免页面缩放后展示错位。
  private handleChartResize = () => {
    this.hotQuestionChart && this.hotQuestionChart.resize()
    this.riskAuditStatusChart && this.riskAuditStatusChart.resize()
    this.approvalTrendChart && this.approvalTrendChart.resize()
    this.riskActionTrendChart && this.riskActionTrendChart.resize()
    this.riskActionApprovalChart && this.riskActionApprovalChart.resize()
    this.fallbackTrendChart && this.fallbackTrendChart.resize()
  }

  // 统一销毁图表实例，避免页面切换后残留监听与内存占用。
  private disposeCharts() {
    if (this.hotQuestionChart) {
      this.hotQuestionChart.dispose()
      this.hotQuestionChart = null
    }
    if (this.riskAuditStatusChart) {
      this.riskAuditStatusChart.dispose()
      this.riskAuditStatusChart = null
    }
    if (this.approvalTrendChart) {
      this.approvalTrendChart.dispose()
      this.approvalTrendChart = null
    }
    if (this.riskActionTrendChart) {
      this.riskActionTrendChart.dispose()
      this.riskActionTrendChart = null
    }
    if (this.riskActionApprovalChart) {
      this.riskActionApprovalChart.dispose()
      this.riskActionApprovalChart = null
    }
    if (this.fallbackTrendChart) {
      this.fallbackTrendChart.dispose()
      this.fallbackTrendChart = null
    }
  }

  // 统一创建知识库表单默认值。
  private createKnowledgeForm() {
    return {
      id: '',
      title: '',
      docType: 'faq',
      sourceType: 'manual',
      sourceId: '',
      content: '',
      status: 1,
      statusSwitch: true
    }
  }

  // 统一创建高风险审计表单默认值。
  private createRiskAuditForm() {
    return {
      assistantMessageId: '',
      userQuestion: '',
      assistantReply: '',
      auditStatus: 1,
      auditRemark: ''
    }
  }

  // 统一创建高风险审批表单默认值。
  private createRiskApprovalForm() {
    return {
      assistantMessageId: '',
      userQuestion: '',
      assistantReply: '',
      approvalStatus: 1,
      approvalRemark: ''
    }
  }
}
</script>

<style lang="scss" scoped>
.ai-operation-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.page-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: #909399;
  line-height: 22px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card,
.panel-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 18px;
  box-sizing: border-box;
}

.summary-label {
  font-size: 13px;
  color: #909399;
  line-height: 20px;
}

.summary-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 36px;
}

.summary-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 18px;
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.panel-card-full {
  grid-column: 1 / -1;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.panel-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.panel-header-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-extra {
  margin-left: 12px;
  font-size: 13px;
  font-weight: 400;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.risk-audit-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.risk-audit-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.approval-summary {
  margin-top: -4px;
}

.risk-summary-card {
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.risk-summary-card.active {
  border-color: #f56c6c;
  background: #fef0f0;
}

.risk-summary-label {
  font-size: 13px;
  color: #909399;
  line-height: 20px;
}

.risk-summary-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  line-height: 32px;
}

.risk-filter-item {
  width: 180px;
}

.risk-filter-keyword {
  width: 240px;
}

.risk-filter-date {
  width: 340px;
}

.risk-audit-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.conversation-panel {
  margin-bottom: 20px;
}

.rank-index {
  font-weight: 600;
  color: #303133;
}
</style>
