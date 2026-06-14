package com.community.ai.tool;

import com.community.ai.mapper.AiKnowledgeDocumentMapper;
import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AiKnowledgeDocument;
import com.community.vo.ActivityKnowledgeVO;
import com.community.vo.OrderStatusVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityToolsTest {

    @Mock
    private AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;

    @Mock
    private OrderTools orderTools;

    @Mock
    private CustomerAiToolLogService customerAiToolLogService;

    @InjectMocks
    private ActivityTools activityTools;

    @Test
    void shouldReturnActivityKnowledgeWithLatestOrderAmountContext() {
        AiKnowledgeDocument document = new AiKnowledgeDocument();
        document.setId(1L);
        document.setTitle("优惠券使用说明");
        document.setDocType("activity");
        document.setContent("优惠券是否可用取决于有效期和订单金额门槛。");

        OrderStatusVO orderStatusVO = new OrderStatusVO();
        orderStatusVO.setAmount(new BigDecimal("58.00"));

        when(aiKnowledgeDocumentMapper.listEnabledByDocType("activity")).thenReturn(Collections.singletonList(document));
        when(orderTools.queryLatestOrder(1001L, null)).thenReturn(orderStatusVO);

        ActivityKnowledgeVO result = activityTools.queryActivityKnowledge(1001L, 2001L, "优惠券");

        assertNotNull(result);
        assertEquals("优惠券使用说明", result.getTitle());
        assertEquals("activity", result.getDocType());
        verify(customerAiToolLogService).saveToolCall(any(), any(), any(), any(), any(), any(boolean.class), any());
    }
}
