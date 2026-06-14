package com.community.ai.controller.admin;

import com.community.ai.service.CustomerAiAdminService;
import com.community.dto.AiRiskAuditPageQueryDTO;
import com.community.dto.AiRiskApprovalUpdateDTO;
import com.community.dto.AiRiskAuditUpdateDTO;
import com.community.entity.AiKnowledgeDocument;
import com.community.entity.AiToolCallLog;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.vo.AiConversationVO;
import com.community.vo.AiFallbackAnalysisVO;
import com.community.vo.AiFallbackTrendItemVO;
import com.community.vo.AiHotQuestionVO;
import com.community.vo.AiMessageVO;
import com.community.vo.AiRiskActionTrendItemVO;
import com.community.vo.AiRiskActionApprovalVO;
import com.community.vo.AiRiskApprovalOverviewVO;
import com.community.vo.AiRiskAuditSummaryVO;
import com.community.vo.AiRiskAuditTrendItemVO;
import com.community.vo.AiRiskAuditVO;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ai/customer")
@Tag(name = "管理端-客服AI运营接口")
public class CustomerAiAdminController {

    private final CustomerAiAdminService customerAiAdminService;

    public CustomerAiAdminController(CustomerAiAdminService customerAiAdminService) {
        this.customerAiAdminService = customerAiAdminService;
    }

    @GetMapping("/conversations")
    @Operation(summary = "查询AI会话列表")
    @PreAuthorize("@permissionService.hasAuthority('ai:conversation:list')")
    public Result<List<AiConversationVO>> conversations() {
        return Result.success(customerAiAdminService.listConversations());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "查询AI会话消息")
    @PreAuthorize("@permissionService.hasAuthority('ai:conversation:detail')")
    public Result<List<AiMessageVO>> messages(@PathVariable Long conversationId) {
        return Result.success(customerAiAdminService.listMessages(conversationId));
    }

    @GetMapping("/knowledge")
    @Operation(summary = "查询知识库文档")
    @PreAuthorize("@permissionService.hasAuthority('ai:knowledge:list')")
    public Result<List<AiKnowledgeDocument>> knowledge() {
        return Result.success(customerAiAdminService.listKnowledgeDocuments());
    }

    @PostMapping("/knowledge")
    @Operation(summary = "新增或更新知识库文档")
    @PreAuthorize("@permissionService.hasAuthority('ai:knowledge:save')")
    public Result<Map<String, Long>> saveKnowledge(@RequestBody AiKnowledgeDocument document) {
        Long id = customerAiAdminService.saveKnowledgeDocument(document);
        Map<String, Long> data = new HashMap<>();
        data.put("documentId", id);
        return Result.success(data);
    }

    @PutMapping("/knowledge/{id}/status/{status}")
    @Operation(summary = "更新知识库文档状态")
    @PreAuthorize("@permissionService.hasAuthority('ai:knowledge:status')")
    public Result<Void> updateKnowledgeStatus(@PathVariable Long id, @PathVariable Integer status) {
        customerAiAdminService.updateKnowledgeStatus(id, status);
        return Result.success();
    }

    @GetMapping("/tool-logs")
    @Operation(summary = "查询工具调用日志")
    @PreAuthorize("@permissionService.hasAuthority('ai:tool-log:list')")
    public Result<List<AiToolCallLog>> toolLogs() {
        return Result.success(customerAiAdminService.listToolLogs());
    }

    @GetMapping("/statistics/hot-questions")
    @Operation(summary = "查询热门问题统计")
    @PreAuthorize("@permissionService.hasAuthority('ai:statistics:hot-questions')")
    public Result<List<AiHotQuestionVO>> hotQuestions(@RequestParam(required = false) Integer limit) {
        return Result.success(customerAiAdminService.listHotQuestions(limit));
    }

    @GetMapping("/statistics/fallback-questions")
    @Operation(summary = "查询未命中问题分析")
    @PreAuthorize("@permissionService.hasAuthority('ai:statistics:fallback')")
    public Result<List<AiFallbackAnalysisVO>> fallbackQuestions(@RequestParam(required = false) Integer limit) {
        return Result.success(customerAiAdminService.listFallbackQuestions(limit));
    }

    @GetMapping("/statistics/risk-audits")
    @Operation(summary = "查询高风险回答审计")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:audit-list')")
    public Result<List<AiRiskAuditVO>> riskAudits(@RequestParam(required = false) Integer limit) {
        return Result.success(customerAiAdminService.listRiskAudits(limit));
    }

    @GetMapping("/statistics/risk-audits/page")
    @Operation(summary = "分页查询高风险回答审计")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:audit-page')")
    public Result<PageResult> riskAuditPage(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.pageRiskAudits(queryDTO));
    }

    @GetMapping("/statistics/risk-audits/summary")
    @Operation(summary = "查询高风险回答审计状态统计")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:audit-summary')")
    public Result<AiRiskAuditSummaryVO> riskAuditSummary(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.getRiskAuditSummary(queryDTO));
    }

    @GetMapping("/statistics/risk-audits/approval-overview")
    @Operation(summary = "查询高风险动作审批概览")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:approval-overview')")
    public Result<AiRiskApprovalOverviewVO> riskApprovalOverview(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.getRiskApprovalOverview(queryDTO));
    }

    @GetMapping("/statistics/risk-audits/approval-trend")
    @Operation(summary = "查询高风险动作审批趋势")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:approval-trend')")
    public Result<List<AiRiskAuditTrendItemVO>> riskApprovalTrend(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.listRiskApprovalTrend(queryDTO));
    }

    @GetMapping("/statistics/risk-audits/action-trend")
    @Operation(summary = "查询高风险动作类型趋势")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:action-trend')")
    public Result<List<AiRiskActionTrendItemVO>> riskActionTrend(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.listRiskActionTrend(queryDTO));
    }

    @GetMapping("/statistics/risk-audits/action-approval-stats")
    @Operation(summary = "查询高风险动作审批通过率对比")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:action-approval-stats')")
    public Result<List<AiRiskActionApprovalVO>> riskActionApprovalStats(AiRiskAuditPageQueryDTO queryDTO) {
        return Result.success(customerAiAdminService.listRiskActionApprovalStats(queryDTO));
    }

    @GetMapping("/statistics/fallback-trend")
    @Operation(summary = "查询未命中问题趋势")
    @PreAuthorize("@permissionService.hasAuthority('ai:statistics:fallback-trend')")
    public Result<List<AiFallbackTrendItemVO>> fallbackTrend(@RequestParam(required = false) Integer limit) {
        return Result.success(customerAiAdminService.listFallbackTrend(limit));
    }

    @GetMapping("/statistics/risk-audits/export")
    @Operation(summary = "导出高风险回答审计")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:export')")
    public void exportRiskAudits(AiRiskAuditPageQueryDTO queryDTO, HttpServletResponse response) {
        customerAiAdminService.exportRiskAudits(queryDTO, response);
    }

    @PutMapping("/statistics/risk-audits")
    @Operation(summary = "更新高风险回答审计状态")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:audit-handle')")
    public Result<Void> updateRiskAudit(@Valid @RequestBody AiRiskAuditUpdateDTO dto) {
        customerAiAdminService.updateRiskAudit(dto);
        return Result.success();
    }

    @PutMapping("/statistics/risk-audits/approval")
    @Operation(summary = "更新高风险动作审批状态")
    @PreAuthorize("@permissionService.hasAuthority('ai:risk:approve')")
    public Result<Void> updateRiskApproval(@Valid @RequestBody AiRiskApprovalUpdateDTO dto) {
        customerAiAdminService.updateRiskApproval(dto);
        return Result.success();
    }
}


