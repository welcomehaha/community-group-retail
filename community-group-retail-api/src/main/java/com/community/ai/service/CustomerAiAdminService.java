package com.community.ai.service;

import com.community.dto.AiRiskAuditPageQueryDTO;
import com.community.dto.AiRiskApprovalUpdateDTO;
import com.community.entity.AiKnowledgeDocument;
import com.community.entity.AiToolCallLog;
import com.community.dto.AiRiskAuditUpdateDTO;
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
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface CustomerAiAdminService {

    List<AiConversationVO> listConversations();

    List<AiMessageVO> listMessages(Long conversationId);

    List<AiKnowledgeDocument> listKnowledgeDocuments();

    Long saveKnowledgeDocument(AiKnowledgeDocument document);

    void updateKnowledgeStatus(Long id, Integer status);

    List<AiToolCallLog> listToolLogs();

    List<AiHotQuestionVO> listHotQuestions(Integer limit);

    List<AiFallbackAnalysisVO> listFallbackQuestions(Integer limit);

    List<AiRiskAuditVO> listRiskAudits(Integer limit);

    PageResult pageRiskAudits(AiRiskAuditPageQueryDTO queryDTO);

    AiRiskAuditSummaryVO getRiskAuditSummary(AiRiskAuditPageQueryDTO queryDTO);

    AiRiskApprovalOverviewVO getRiskApprovalOverview(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskAuditTrendItemVO> listRiskApprovalTrend(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskActionTrendItemVO> listRiskActionTrend(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskActionApprovalVO> listRiskActionApprovalStats(AiRiskAuditPageQueryDTO queryDTO);

    List<AiFallbackTrendItemVO> listFallbackTrend(Integer limit);

    void updateRiskAudit(AiRiskAuditUpdateDTO dto);

    void updateRiskApproval(AiRiskApprovalUpdateDTO dto);

    void exportRiskAudits(AiRiskAuditPageQueryDTO queryDTO, HttpServletResponse response);
}
