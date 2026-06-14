package com.community.service.impl;

import com.community.ai.service.impl.CustomerAiAdminServiceImpl;
import com.community.ai.mapper.AiConversationMapper;
import com.community.ai.mapper.AiKnowledgeDocumentMapper;
import com.community.ai.mapper.AiMessageMapper;
import com.community.ai.mapper.AiToolCallLogMapper;
import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.context.BaseContext;
import com.community.dto.AiRiskApprovalUpdateDTO;
import com.community.dto.AiRiskAuditUpdateDTO;
import com.community.entity.AiMessage;
import com.community.exception.BaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAiAdminServiceImplTest {

    @Mock
    private AiConversationMapper aiConversationMapper;

    @Mock
    private AiMessageMapper aiMessageMapper;

    @Mock
    private AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;

    @Mock
    private AiToolCallLogMapper aiToolCallLogMapper;

    @Mock
    private CustomerAiKnowledgeService customerAiKnowledgeService;

    @InjectMocks
    private CustomerAiAdminServiceImpl customerAiAdminService;

    private AiRiskAuditUpdateDTO auditUpdateDTO;
    private AiRiskApprovalUpdateDTO approvalUpdateDTO;

    @BeforeEach
    void setUp() {
        auditUpdateDTO = new AiRiskAuditUpdateDTO();
        auditUpdateDTO.setAssistantMessageId(1001L);
        auditUpdateDTO.setAuditStatus(1);
        auditUpdateDTO.setAuditRemark("人工确认高风险");

        approvalUpdateDTO = new AiRiskApprovalUpdateDTO();
        approvalUpdateDTO.setAssistantMessageId(1001L);
        approvalUpdateDTO.setApprovalStatus(1);
        approvalUpdateDTO.setApprovalRemark("审批通过");
    }

    @AfterEach
    void tearDown() {
        // 清理线程上下文，避免测试间相互污染
        BaseContext.removeCurrentId();
    }

    @Test
    void shouldRejectAuditWhenMessageNotFound() {
        when(aiMessageMapper.getById(1001L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerAiAdminService.updateRiskAudit(auditUpdateDTO));

        assertEquals("高风险消息不存在", exception.getMessage());
        verify(aiMessageMapper, never()).updateRiskAudit(anyLong(), eq(1), eq("人工确认高风险"));
    }

    @Test
    void shouldRejectAuditWhenMessageIsUserRole() {
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("user", null, null, "普通用户问题", 0, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerAiAdminService.updateRiskAudit(auditUpdateDTO));

        assertEquals("当前消息不是可审计的高风险助手消息", exception.getMessage());
    }

    @Test
    void shouldRejectAuditWhenAssistantMessageIsNotRiskMessage() {
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", null, null, "普通回复内容", 0, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerAiAdminService.updateRiskAudit(auditUpdateDTO));

        assertEquals("当前消息不是可审计的高风险助手消息", exception.getMessage());
    }

    @Test
    void shouldUpdateAuditWhenRiskAssistantMessageMatchesRule() {
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", "action", null, "请确认是否取消订单", 0, 0));
        when(aiMessageMapper.updateRiskAudit(1001L, 1, "人工确认高风险")).thenReturn(1);

        assertDoesNotThrow(() -> customerAiAdminService.updateRiskAudit(auditUpdateDTO));

        verify(aiMessageMapper).updateRiskAudit(1001L, 1, "人工确认高风险");
    }

    @Test
    void shouldRejectApprovalWhenAuditNotHandledYet() {
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", "action", null, "请确认是否取消订单", 0, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerAiAdminService.updateRiskApproval(approvalUpdateDTO));

        assertEquals("请先完成高风险审计处理，再进行审批", exception.getMessage());
        verify(aiMessageMapper, never()).updateRiskApproval(anyLong(), eq(1), eq("审批通过"), anyLong());
    }

    @Test
    void shouldRejectApprovalWhenAlreadyApproved() {
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", "action", null, "请确认是否取消订单", 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerAiAdminService.updateRiskApproval(approvalUpdateDTO));

        assertEquals("该高风险记录已完成审批，请勿重复提交", exception.getMessage());
    }

    @Test
    void shouldReturnConflictWhenConcurrentApprovalOverridesState() {
        BaseContext.setCurrentId(9527L);
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", "action", null, "请确认是否取消订单", 1, 0));
        when(aiMessageMapper.updateRiskApproval(1001L, 1, "审批通过", 9527L)).thenReturn(0);

        BaseException exception = assertThrows(BaseException.class,
            () -> customerAiAdminService.updateRiskApproval(approvalUpdateDTO));

        assertEquals("该高风险记录已被其他管理员审批，请刷新后重试", exception.getMessage());
    }

    @Test
    void shouldUpdateApprovalWhenRiskMessageIsPendingAndAudited() {
        BaseContext.setCurrentId(9527L);
        when(aiMessageMapper.getById(1001L)).thenReturn(buildMessage("assistant", "action", null, "请确认是否取消订单", 1, 0));
        when(aiMessageMapper.updateRiskApproval(1001L, 1, "审批通过", 9527L)).thenReturn(1);

        assertDoesNotThrow(() -> customerAiAdminService.updateRiskApproval(approvalUpdateDTO));

        verify(aiMessageMapper).updateRiskApproval(1001L, 1, "审批通过", 9527L);
    }

    /**
     * 构造测试消息，覆盖角色、风险命中条件和状态机字段
     */
    private AiMessage buildMessage(String role, String toolName, String actionType, String content,
                                   Integer auditStatus, Integer approvalStatus) {
        AiMessage message = new AiMessage();
        message.setId(1001L);
        message.setRole(role);
        message.setToolName(toolName);
        message.setActionType(actionType);
        message.setContent(content);
        message.setAuditStatus(auditStatus);
        message.setApprovalStatus(approvalStatus);
        return message;
    }
}
