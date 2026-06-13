package com.community.service.impl;

import com.community.ai.mapper.AiConversationMapper;
import com.community.ai.mapper.AiMessageMapper;
import com.community.ai.router.CustomerAiIntentClassifier;
import com.community.ai.router.CustomerAiScenePromptBuilder;
import com.community.ai.router.CustomerAiUserStateAssembler;
import com.community.ai.service.CustomerAiKnowledgeService;
import com.community.ai.service.CustomerAiLlmService;
import com.community.ai.service.impl.CustomerAiServiceImpl;
import com.community.ai.tool.CustomerRecordTools;
import com.community.ai.tool.OrderTools;
import com.community.ai.tool.ProductTools;
import com.community.ai.tool.StoreTools;
import com.community.ai.tool.ActivityTools;
import com.community.ai.tool.DeliveryScopeTools;
import com.community.entity.AiConversation;
import com.community.entity.AiMessage;
import com.community.properties.AiProperties;
import com.community.vo.ActivityKnowledgeVO;
import com.community.vo.AiChatVO;
import com.community.vo.DeliveryScopeCheckVO;
import com.community.vo.OrderStatusVO;
import com.community.vo.StoreStatusVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAiServiceImplTest {

    @Mock
    private AiConversationMapper aiConversationMapper;

    @Mock
    private AiMessageMapper aiMessageMapper;

    @Mock
    private CustomerAiKnowledgeService customerAiKnowledgeService;

    @Mock
    private CustomerAiLlmService customerAiLlmService;

    @Mock
    private OrderTools orderTools;

    @Mock
    private ProductTools productTools;

    @Mock
    private StoreTools storeTools;

    @Mock
    private ActivityTools activityTools;

    @Mock
    private DeliveryScopeTools deliveryScopeTools;

    @Mock
    private CustomerRecordTools customerRecordTools;

    @Spy
    private CustomerAiIntentClassifier customerAiIntentClassifier = new CustomerAiIntentClassifier();

    @Spy
    private CustomerAiScenePromptBuilder customerAiScenePromptBuilder = new CustomerAiScenePromptBuilder();

    @Mock
    private CustomerAiUserStateAssembler customerAiUserStateAssembler;

    @Spy
    private AiProperties aiProperties = new AiProperties();

    @InjectMocks
    private CustomerAiServiceImpl customerAiService;

    @Test
    void shouldRouteOrderQuestionAndPassDynamicPromptToLlm() {
        AiConversation conversation = new AiConversation();
        conversation.setId(1001L);
        conversation.setUserId(2001L);

        OrderStatusVO orderStatusVO = new OrderStatusVO();
        orderStatusVO.setOrderNumber("20240611001");
        orderStatusVO.setStatusLabel("配送中");
        orderStatusVO.setPayStatusLabel("已支付");

        com.community.ai.dto.AiLlmReply aiLlmReply = new com.community.ai.dto.AiLlmReply();
        aiLlmReply.setSuccess(true);
        aiLlmReply.setContent("订单正在配送中，请耐心等待。");

        when(aiConversationMapper.getByIdAndUserId(1001L, 2001L)).thenReturn(conversation);
        when(orderTools.queryOrderStatus(2001L, 1001L, "20240611001")).thenReturn(orderStatusVO);
        when(orderTools.queryOrderStatusSilently(2001L, "20240611001")).thenReturn(orderStatusVO);
        when(aiMessageMapper.listByConversationId(1001L)).thenReturn(Collections.emptyList());
        when(customerAiUserStateAssembler.buildStateContext(anyLong(), anyLong(), any())).thenReturn("用户当前关联订单摘要：订单号=20240611001，订单状态=配送中");
        when(customerAiLlmService.chat(eq("帮我看下订单20240611001到哪了"), any(), any(), any())).thenReturn(aiLlmReply);

        AiChatVO result = customerAiService.chat(2001L, 1001L, "帮我看下订单20240611001到哪了");

        assertEquals("order", result.getRoute());
        assertEquals("订单正在配送中，请耐心等待。", result.getAnswer());

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(customerAiLlmService).chat(eq("帮我看下订单20240611001到哪了"), any(), promptCaptor.capture(), any());
        assertTrue(promptCaptor.getValue().contains("当前是订单场景"));
        verify(aiMessageMapper, atLeastOnce()).insert(any(AiMessage.class));
    }

    @Test
    void shouldRequireExplicitConfirmationForCancelAction() {
        AiConversation conversation = new AiConversation();
        conversation.setId(1002L);
        conversation.setUserId(2002L);

        when(aiConversationMapper.getByIdAndUserId(1002L, 2002L)).thenReturn(conversation);

        AiChatVO result = customerAiService.chat(2002L, 1002L, "取消订单 20240611002");

        assertEquals("action", result.getRoute());
        assertEquals("cancel_order", result.getActionType());
        assertTrue(result.getAnswer().contains("高风险操作"));
    }

    @Test
    void shouldRouteActivityQuestionToActivityTool() {
        AiConversation conversation = new AiConversation();
        conversation.setId(1003L);
        conversation.setUserId(2003L);

        ActivityKnowledgeVO activityKnowledgeVO = new ActivityKnowledgeVO();
        activityKnowledgeVO.setTitle("优惠券使用说明");
        activityKnowledgeVO.setContent("优惠券可用与否取决于有效期、适用商品和订单金额门槛。");

        when(aiConversationMapper.getByIdAndUserId(1003L, 2003L)).thenReturn(conversation);
        when(activityTools.queryActivityKnowledge(2003L, 1003L, "新人优惠券怎么用")).thenReturn(activityKnowledgeVO);
        when(aiMessageMapper.listByConversationId(1003L)).thenReturn(Collections.emptyList());
        when(customerAiLlmService.chat(eq("新人优惠券怎么用"), any(), any(), any())).thenReturn(null);

        AiChatVO result = customerAiService.chat(2003L, 1003L, "新人优惠券怎么用");

        assertEquals("activity", result.getRoute());
        assertTrue(result.getAnswer().contains("优惠券"));
    }

    @Test
    void shouldBuildStoreReplyWithDeliveryScopeHint() {
        AiConversation conversation = new AiConversation();
        conversation.setId(1004L);
        conversation.setUserId(2004L);

        StoreStatusVO storeStatusVO = new StoreStatusVO();
        storeStatusVO.setStatus(1);
        storeStatusVO.setStatusLabel("营业中");
        storeStatusVO.setMessage("门店当前可正常接单。");

        DeliveryScopeCheckVO deliveryScopeCheckVO = new DeliveryScopeCheckVO();
        deliveryScopeCheckVO.setScopeStatus("unknown");
        deliveryScopeCheckVO.setDistanceMeters(null);
        deliveryScopeCheckVO.setMessage("当前系统未完成地图服务配置，暂时无法自动判断配送范围，建议转人工或由下单页再次校验。");

        when(aiConversationMapper.getByIdAndUserId(1004L, 2004L)).thenReturn(conversation);
        when(storeTools.queryStoreStatus(2004L, 1004L)).thenReturn(storeStatusVO);
        when(deliveryScopeTools.checkDeliveryScope(2004L, 1004L)).thenReturn(deliveryScopeCheckVO);
        when(aiMessageMapper.listByConversationId(1004L)).thenReturn(Collections.emptyList());
        when(customerAiUserStateAssembler.buildStateContext(anyLong(), anyLong(), any())).thenReturn("门店当前状态：营业中；配送范围评估：unknown");
        when(customerAiLlmService.chat(any(), any(), any(), any())).thenReturn(null);

        AiChatVO result = customerAiService.chat(2004L, 1004L, "门店现在营业吗，我这里能送到吗");

        assertEquals("store", result.getRoute());
        assertTrue(result.getAnswer().contains("营业中"));
        assertTrue(result.getAnswer().contains("不能自动确认"));
    }
}
