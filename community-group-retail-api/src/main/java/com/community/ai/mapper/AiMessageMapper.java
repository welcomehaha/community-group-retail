package com.community.ai.mapper;

import com.community.entity.AiMessage;
import com.community.dto.AiRiskAuditPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.community.vo.AiFallbackAnalysisVO;
import com.community.vo.AiHotQuestionVO;
import com.community.vo.AiRiskAuditVO;
import com.community.vo.AiRiskAuditSummaryVO;
import com.community.vo.AiRiskAuditTrendItemVO;
import com.community.vo.AiRiskApprovalOverviewVO;
import com.community.vo.AiRiskActionTrendItemVO;
import com.community.vo.AiRiskActionApprovalVO;
import com.community.vo.AiFallbackTrendItemVO;

import java.util.List;

@Mapper
public interface AiMessageMapper {

    void insert(AiMessage message);

    List<AiMessage> listByConversationId(Long conversationId);

    AiMessage getLastMessage(Long conversationId);

    AiMessage getById(@Param("id") Long id);

    void deleteByConversationId(@Param("conversationId") Long conversationId);

    List<AiHotQuestionVO> listHotQuestions(@Param("limit") Integer limit);

    List<AiFallbackAnalysisVO> listFallbackQuestions(@Param("limit") Integer limit);

    List<AiRiskAuditVO> listRiskAudits(@Param("limit") Integer limit);

    List<AiRiskAuditVO> pageRiskAudits(AiRiskAuditPageQueryDTO queryDTO);

    AiRiskAuditSummaryVO getRiskAuditSummary(AiRiskAuditPageQueryDTO queryDTO);

    AiRiskApprovalOverviewVO getRiskApprovalOverview(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskAuditTrendItemVO> listRiskApprovalTrend(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskActionTrendItemVO> listRiskActionTrend(AiRiskAuditPageQueryDTO queryDTO);

    List<AiRiskActionApprovalVO> listRiskActionApprovalStats(AiRiskAuditPageQueryDTO queryDTO);

    List<AiFallbackTrendItemVO> listFallbackTrend(@Param("limit") Integer limit);

    int updateRiskAudit(@Param("assistantMessageId") Long assistantMessageId,
                        @Param("auditStatus") Integer auditStatus,
                        @Param("auditRemark") String auditRemark);

    int updateRiskApproval(@Param("assistantMessageId") Long assistantMessageId,
                           @Param("approvalStatus") Integer approvalStatus,
                           @Param("approvalRemark") String approvalRemark,
                           @Param("approvalUserId") Long approvalUserId);
}
