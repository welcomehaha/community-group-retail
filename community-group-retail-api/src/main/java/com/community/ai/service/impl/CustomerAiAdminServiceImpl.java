package com.community.ai.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.ai.mapper.AiConversationMapper;
import com.community.ai.mapper.AiKnowledgeDocumentMapper;
import com.community.ai.mapper.AiMessageMapper;
import com.community.ai.mapper.AiToolCallLogMapper;
import com.community.ai.service.CustomerAiAdminService;
import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.context.BaseContext;
import com.community.dto.AiRiskAuditPageQueryDTO;
import com.community.dto.AiRiskApprovalUpdateDTO;
import com.community.dto.AiRiskAuditUpdateDTO;
import com.community.entity.AiConversation;
import com.community.entity.AiKnowledgeDocument;
import com.community.entity.AiMessage;
import com.community.entity.AiToolCallLog;
import com.community.exception.BaseException;
import com.community.result.PageResult;
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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerAiAdminServiceImpl implements CustomerAiAdminService {

    /**
     * 导出最大条数，避免一次性加载过大数据集导致内存压力过高
     */
    private static final int EXPORT_MAX_ROWS = 5000;

    private final AiConversationMapper aiConversationMapper;
    private final AiMessageMapper aiMessageMapper;
    private final AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;
    private final AiToolCallLogMapper aiToolCallLogMapper;
    private final CustomerAiKnowledgeService customerAiKnowledgeService;

    public CustomerAiAdminServiceImpl(AiConversationMapper aiConversationMapper,
                                      AiMessageMapper aiMessageMapper,
                                      AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper,
                                      AiToolCallLogMapper aiToolCallLogMapper,
                                      CustomerAiKnowledgeService customerAiKnowledgeService) {
        this.aiConversationMapper = aiConversationMapper;
        this.aiMessageMapper = aiMessageMapper;
        this.aiKnowledgeDocumentMapper = aiKnowledgeDocumentMapper;
        this.aiToolCallLogMapper = aiToolCallLogMapper;
        this.customerAiKnowledgeService = customerAiKnowledgeService;
    }

    @Override
    public List<AiConversationVO> listConversations() {
        List<AiConversation> conversations = aiConversationMapper.listAll();
        List<AiConversationVO> result = new ArrayList<>();
        for (AiConversation conversation : conversations) {
            AiConversationVO vo = new AiConversationVO();
            vo.setConversationId(conversation.getId());
            vo.setTitle(conversation.getTitle());
            vo.setStatus(conversation.getStatus());
            vo.setCreateTime(conversation.getCreateTime());
            vo.setUpdateTime(conversation.getUpdateTime());
            AiMessage lastMessage = aiMessageMapper.getLastMessage(conversation.getId());
            if (lastMessage != null) {
                vo.setLastMessage(lastMessage.getContent());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<AiMessageVO> listMessages(Long conversationId) {
        List<AiMessage> messages = aiMessageMapper.listByConversationId(conversationId);
        List<AiMessageVO> result = new ArrayList<>();
        for (AiMessage message : messages) {
            AiMessageVO vo = new AiMessageVO();
            BeanUtils.copyProperties(message, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<AiKnowledgeDocument> listKnowledgeDocuments() {
        return aiKnowledgeDocumentMapper.listAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveKnowledgeDocument(AiKnowledgeDocument document) {
        if (document.getId() == null) {
            document.setStatus(document.getStatus() == null ? 1 : document.getStatus());
            document.setVersion(document.getVersion() == null ? 1 : document.getVersion());
            document.setSourceType(StringUtils.hasText(document.getSourceType()) ? document.getSourceType() : "manual");
            document.setDocType(StringUtils.hasText(document.getDocType()) ? document.getDocType() : "faq");
            document.setCreateTime(LocalDateTime.now());
            document.setUpdateTime(LocalDateTime.now());
            aiKnowledgeDocumentMapper.insert(document);
        } else {
            document.setUpdateTime(LocalDateTime.now());
            aiKnowledgeDocumentMapper.update(document);
        }
        customerAiKnowledgeService.rebuildKnowledgeIndex(document.getId());
        return document.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKnowledgeStatus(Long id, Integer status) {
        aiKnowledgeDocumentMapper.updateStatus(id, status);
        customerAiKnowledgeService.rebuildKnowledgeIndex(id);
    }

    @Override
    public List<AiToolCallLog> listToolLogs() {
        return aiToolCallLogMapper.listAll();
    }

    @Override
    public List<AiHotQuestionVO> listHotQuestions(Integer limit) {
        return aiMessageMapper.listHotQuestions(normalizeLimit(limit));
    }

    @Override
    public List<AiFallbackAnalysisVO> listFallbackQuestions(Integer limit) {
        return aiMessageMapper.listFallbackQuestions(normalizeLimit(limit));
    }

    @Override
    public List<AiRiskAuditVO> listRiskAudits(Integer limit) {
        return aiMessageMapper.listRiskAudits(normalizeLimit(limit));
    }

    @Override
    public PageResult pageRiskAudits(AiRiskAuditPageQueryDTO queryDTO) {
        int page = queryDTO == null || queryDTO.getPage() == null || queryDTO.getPage() <= 0 ? 1 : queryDTO.getPage();
        int pageSize = queryDTO == null || queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        PageHelper.startPage(page, pageSize);
        Page<AiRiskAuditVO> result = (Page<AiRiskAuditVO>) aiMessageMapper.pageRiskAudits(queryDTO);
        return new PageResult(result.getTotal(), result.getResult());
    }

    @Override
    public AiRiskAuditSummaryVO getRiskAuditSummary(AiRiskAuditPageQueryDTO queryDTO) {
        AiRiskAuditSummaryVO summary = aiMessageMapper.getRiskAuditSummary(queryDTO);
        if (summary == null) {
            summary = new AiRiskAuditSummaryVO();
        }
        summary.setTotalCount(summary.getTotalCount() == null ? 0L : summary.getTotalCount());
        summary.setPendingCount(summary.getPendingCount() == null ? 0L : summary.getPendingCount());
        summary.setConfirmedCount(summary.getConfirmedCount() == null ? 0L : summary.getConfirmedCount());
        summary.setIgnoredCount(summary.getIgnoredCount() == null ? 0L : summary.getIgnoredCount());
        summary.setApprovalPendingCount(summary.getApprovalPendingCount() == null ? 0L : summary.getApprovalPendingCount());
        summary.setApprovalApprovedCount(summary.getApprovalApprovedCount() == null ? 0L : summary.getApprovalApprovedCount());
        summary.setApprovalRejectedCount(summary.getApprovalRejectedCount() == null ? 0L : summary.getApprovalRejectedCount());
        return summary;
    }

    @Override
    public AiRiskApprovalOverviewVO getRiskApprovalOverview(AiRiskAuditPageQueryDTO queryDTO) {
        AiRiskApprovalOverviewVO overview = aiMessageMapper.getRiskApprovalOverview(queryDTO);
        if (overview == null) {
            overview = new AiRiskApprovalOverviewVO();
        }
        long approvedCount = overview.getApprovalApprovedCount() == null ? 0L : overview.getApprovalApprovedCount();
        long rejectedCount = overview.getApprovalRejectedCount() == null ? 0L : overview.getApprovalRejectedCount();
        long totalCount = overview.getApprovalTotalCount() == null ? approvedCount + rejectedCount : overview.getApprovalTotalCount();
        overview.setApprovalApprovedCount(approvedCount);
        overview.setApprovalRejectedCount(rejectedCount);
        overview.setApprovalTotalCount(totalCount);
        if (totalCount <= 0) {
            overview.setApprovalPassRate(BigDecimal.ZERO);
        } else {
            overview.setApprovalPassRate(BigDecimal.valueOf(approvedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
        }
        return overview;
    }

    @Override
    public List<AiRiskAuditTrendItemVO> listRiskApprovalTrend(AiRiskAuditPageQueryDTO queryDTO) {
        return aiMessageMapper.listRiskApprovalTrend(queryDTO);
    }

    @Override
    public List<AiRiskActionTrendItemVO> listRiskActionTrend(AiRiskAuditPageQueryDTO queryDTO) {
        return aiMessageMapper.listRiskActionTrend(queryDTO);
    }

    @Override
    public List<AiRiskActionApprovalVO> listRiskActionApprovalStats(AiRiskAuditPageQueryDTO queryDTO) {
        return aiMessageMapper.listRiskActionApprovalStats(queryDTO);
    }

    @Override
    public List<AiFallbackTrendItemVO> listFallbackTrend(Integer limit) {
        return aiMessageMapper.listFallbackTrend(normalizeLimit(limit));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRiskAudit(AiRiskAuditUpdateDTO dto) {
        if (dto == null || dto.getAssistantMessageId() == null) {
            throw new IllegalArgumentException("审计消息不能为空");
        }
        if (dto.getAuditStatus() == null || (dto.getAuditStatus() != 1 && dto.getAuditStatus() != 2)) {
            throw new IllegalArgumentException("审计状态仅支持已确认或已忽略");
        }
        AiMessage message = requireRiskAssistantMessage(dto.getAssistantMessageId());
        int updatedRows = aiMessageMapper.updateRiskAudit(message.getId(), dto.getAuditStatus(), dto.getAuditRemark());
        if (updatedRows <= 0) {
            throw new BaseException("高风险审计记录不存在或已发生变化，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRiskApproval(AiRiskApprovalUpdateDTO dto) {
        if (dto == null || dto.getAssistantMessageId() == null) {
            throw new IllegalArgumentException("审批消息不能为空");
        }
        if (dto.getApprovalStatus() == null || (dto.getApprovalStatus() != 1 && dto.getApprovalStatus() != 2)) {
            throw new IllegalArgumentException("审批状态仅支持已通过或已驳回");
        }
        if (!StringUtils.hasText(dto.getApprovalRemark())) {
            throw new IllegalArgumentException("审批备注不能为空");
        }
        AiMessage message = requireRiskAssistantMessage(dto.getAssistantMessageId());
        if (message.getAuditStatus() == null || message.getAuditStatus() == 0) {
            throw new IllegalArgumentException("请先完成高风险审计处理，再进行审批");
        }
        if (message.getApprovalStatus() != null && message.getApprovalStatus() != 0) {
            throw new IllegalArgumentException("该高风险记录已完成审批，请勿重复提交");
        }

        // 审批更新增加待审批状态约束，避免并发场景下后写覆盖前写
        int updatedRows = aiMessageMapper.updateRiskApproval(dto.getAssistantMessageId(), dto.getApprovalStatus(),
            dto.getApprovalRemark(), BaseContext.getCurrentId());
        if (updatedRows <= 0) {
            throw new BaseException("该高风险记录已被其他管理员审批，请刷新后重试");
        }
    }

    @Override
    public void exportRiskAudits(AiRiskAuditPageQueryDTO queryDTO, HttpServletResponse response) {
        // 导出时强制限制总量，避免全量查询与内存型工作簿放大资源消耗
        PageHelper.startPage(1, EXPORT_MAX_ROWS + 1, false);
        List<AiRiskAuditVO> queriedRecords = aiMessageMapper.pageRiskAudits(queryDTO);
        if (queriedRecords.size() > EXPORT_MAX_ROWS) {
            throw new IllegalArgumentException("导出记录数超过" + EXPORT_MAX_ROWS + "条，请缩小筛选范围后重试");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=ai-risk-audits.xlsx");

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("高风险审计");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("会话ID");
            header.createCell(1).setCellValue("用户问题");
            header.createCell(2).setCellValue("AI回答");
            header.createCell(3).setCellValue("路由");
            header.createCell(4).setCellValue("动作类型");
            header.createCell(5).setCellValue("风险原因");
            header.createCell(6).setCellValue("处理状态");
            header.createCell(7).setCellValue("处理备注");
            header.createCell(8).setCellValue("审批状态");
            header.createCell(9).setCellValue("审批备注");
            header.createCell(10).setCellValue("审批人ID");
            header.createCell(11).setCellValue("审批时间");
            header.createCell(12).setCellValue("时间");

            for (int i = 0; i < queriedRecords.size(); i++) {
                AiRiskAuditVO item = queriedRecords.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getConversationId() == null ? "" : String.valueOf(item.getConversationId()));
                row.createCell(1).setCellValue(defaultString(item.getUserQuestion()));
                row.createCell(2).setCellValue(defaultString(item.getAssistantReply()));
                row.createCell(3).setCellValue(defaultString(item.getRoute()));
                row.createCell(4).setCellValue(formatActionType(item.getActionType()));
                row.createCell(5).setCellValue(defaultString(item.getRiskReason()));
                row.createCell(6).setCellValue(formatAuditStatus(item.getAuditStatus()));
                row.createCell(7).setCellValue(defaultString(item.getAuditRemark()));
                row.createCell(8).setCellValue(formatApprovalStatus(item.getApprovalStatus()));
                row.createCell(9).setCellValue(defaultString(item.getApprovalRemark()));
                row.createCell(10).setCellValue(item.getApprovalUserId() == null ? "" : String.valueOf(item.getApprovalUserId()));
                row.createCell(11).setCellValue(item.getApprovalTime() == null ? "" : item.getApprovalTime().toString());
                row.createCell(12).setCellValue(item.getCreateTime() == null ? "" : item.getCreateTime().toString());
            }

            // 使用固定列宽替代 autoSize，降低大数据量导出时的 CPU 与内存开销
            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 40 * 256);
            sheet.setColumnWidth(2, 40 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 18 * 256);
            sheet.setColumnWidth(5, 24 * 256);
            sheet.setColumnWidth(6, 14 * 256);
            sheet.setColumnWidth(7, 24 * 256);
            sheet.setColumnWidth(8, 14 * 256);
            sheet.setColumnWidth(9, 24 * 256);
            sheet.setColumnWidth(10, 16 * 256);
            sheet.setColumnWidth(11, 22 * 256);
            sheet.setColumnWidth(12, 22 * 256);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (Exception ex) {
            throw new RuntimeException("导出高风险审计报表失败", ex);
        }
    }

    /**
     * 统一限制统计接口返回数量，避免后台一次拉取过大数据集
     */
    private Integer normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10;
        }
        return Math.min(limit, 50);
    }

    /**
     * 统一校验目标消息必须为命中高风险规则的 AI 助手消息
     */
    private AiMessage requireRiskAssistantMessage(Long assistantMessageId) {
        AiMessage message = aiMessageMapper.getById(assistantMessageId);
        if (message == null) {
            throw new IllegalArgumentException("高风险消息不存在");
        }
        if (!isRiskAssistantMessage(message)) {
            throw new IllegalArgumentException("当前消息不是可审计的高风险助手消息");
        }
        return message;
    }

    /**
     * 与 Mapper 侧高风险命中规则保持一致，避免非目标消息被误写状态
     */
    private boolean isRiskAssistantMessage(AiMessage message) {
        if (message == null || !"assistant".equals(message.getRole())) {
            return false;
        }
        if (StringUtils.hasText(message.getActionType())) {
            return true;
        }
        if ("action".equals(message.getToolName())) {
            return true;
        }
        String content = message.getContent();
        return StringUtils.hasText(content)
            && (content.contains("确认")
            || content.contains("取消订单")
            || content.contains("售后申请")
            || content.contains("催单"));
    }

    private String formatAuditStatus(Integer auditStatus) {
        if (auditStatus == null) {
            return "待处理";
        }
        switch (auditStatus) {
            case 1:
                return "已确认";
            case 2:
                return "已忽略";
            default:
                return "待处理";
        }
    }

    private String formatActionType(String actionType) {
        if (!StringUtils.hasText(actionType)) {
            return "";
        }
        switch (actionType) {
            case "cancel_order":
                return "取消订单";
            case "remind_order":
                return "催单";
            case "after_sale":
                return "售后申请";
            default:
                return actionType;
        }
    }

    private String formatApprovalStatus(Integer approvalStatus) {
        if (approvalStatus == null) {
            return "待审批";
        }
        switch (approvalStatus) {
            case 1:
                return "已通过";
            case 2:
                return "已驳回";
            default:
                return "待审批";
        }
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
