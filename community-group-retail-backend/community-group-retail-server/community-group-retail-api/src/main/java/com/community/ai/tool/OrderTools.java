package com.community.ai.tool;

import com.community.ai.mapper.AiAfterSaleRequestMapper;
import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AiAfterSaleRequest;
import com.community.entity.RetailOrder;
import com.community.mapper.RetailOrderMapper;
import com.community.service.RetailOrderService;
import com.community.vo.OrderStatusVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderTools {

    private final RetailOrderMapper retailOrderMapper;
    private final RetailOrderService retailOrderService;
    private final AiAfterSaleRequestMapper aiAfterSaleRequestMapper;
    private final CustomerAiToolLogService customerAiToolLogService;

    public OrderTools(RetailOrderMapper retailOrderMapper,
                      RetailOrderService retailOrderService,
                      AiAfterSaleRequestMapper aiAfterSaleRequestMapper,
                      CustomerAiToolLogService customerAiToolLogService) {
        this.retailOrderMapper = retailOrderMapper;
        this.retailOrderService = retailOrderService;
        this.aiAfterSaleRequestMapper = aiAfterSaleRequestMapper;
        this.customerAiToolLogService = customerAiToolLogService;
    }

    @Tool(description = "查询当前用户最近一笔订单的状态、支付状态、预计送达时间等信息")
    public OrderStatusVO queryLatestOrder(Long userId, Long conversationId) {
        List<RetailOrder> retailOrder = retailOrderMapper.listRecentByUserId(userId);
        if (retailOrder == null || retailOrder.isEmpty()) {
            saveLog(userId, conversationId, "queryLatestOrder", "{}", null, true, null);
            return null;
        }
        OrderStatusVO vo = toOrderStatusVO(retailOrder.get(0));
        saveLog(userId, conversationId, "queryLatestOrder", "{}", vo, true, null);
        return vo;
    }

    /**
     * 同时兼容业务订单号和订单主键 ID，避免用户提供订单号时查不到数据。
     */
    @Tool(name = "queryOrderStatusByToken", description = "根据订单号查询当前用户订单状态，仅允许查询当前用户自己的订单；当自然语言里提供的是业务订单号时优先调用该工具")
    public OrderStatusVO queryOrderStatus(Long userId, Long conversationId, String orderToken) {
        RetailOrder retailOrder = resolveOwnedOrder(userId, orderToken);
        if (retailOrder == null) {
            saveLog(userId, conversationId, "queryOrderStatus", buildRequest("orderToken", orderToken), null, false, "订单不存在或无权限访问");
            return null;
        }
        OrderStatusVO vo = toOrderStatusVO(retailOrder);
        saveLog(userId, conversationId, "queryOrderStatus", buildRequest("orderToken", orderToken), vo, true, null);
        return vo;
    }

    @Tool(name = "queryOrderStatusById", description = "根据订单ID查询当前用户订单状态，仅允许查询当前用户自己的订单；当已拿到订单主键ID时调用该工具")
    public OrderStatusVO queryOrderStatus(Long userId, Long conversationId, Long orderId) {
        RetailOrder retailOrder = retailOrderMapper.getById(orderId);
        if (retailOrder == null || !userId.equals(retailOrder.getUserId())) {
            saveLog(userId, conversationId, "queryOrderStatus", buildRequest("orderId", orderId), null, false, "订单不存在或无权限访问");
            return null;
        }
        OrderStatusVO vo = toOrderStatusVO(retailOrder);
        saveLog(userId, conversationId, "queryOrderStatus", buildRequest("orderId", orderId), vo, true, null);
        return vo;
    }

    /**
     * 为大模型构建订单上下文时使用静默查询，避免重复写工具日志。
     */
    public OrderStatusVO queryOrderStatusSilently(Long userId, String orderToken) {
        RetailOrder retailOrder = resolveOwnedOrder(userId, orderToken);
        return retailOrder == null ? null : toOrderStatusVO(retailOrder);
    }

    /**
     * 执行催单前先校验订单归属和当前状态，避免 AI 越权或重复催单
     */
    @Tool(name = "remindOrderById", description = "根据订单ID对当前用户自己的订单发起催单，仅支持待接单、已接单、配送中的订单")
    public String remindOrder(Long userId, Long conversationId, Long orderId) {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderId);
        if (retailOrder.getStatus() == null || retailOrder.getStatus() < RetailOrder.TO_BE_CONFIRMED || retailOrder.getStatus() >= RetailOrder.COMPLETED) {
            String message = "当前订单状态不支持催单，只有待接单、已接单、配送中的订单才可以催单。";
            saveLog(userId, conversationId, "remindOrder", buildRequest("orderId", orderId), null, false, message);
            return message;
        }
        retailOrderService.reminder(orderId);
        String message = "催单请求已提交，门店端会收到订单提醒。";
        saveLog(userId, conversationId, "remindOrder", buildRequest("orderId", orderId), buildResponse(message), true, null);
        return message;
    }

    /**
     * 同时支持订单号和订单ID发起催单。
     */
    @Tool(name = "remindOrderByToken", description = "根据订单号对当前用户自己的订单发起催单，仅支持待接单、已接单、配送中的订单")
    public String remindOrder(Long userId, Long conversationId, String orderToken) {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderToken);
        if (retailOrder.getStatus() == null || retailOrder.getStatus() < RetailOrder.TO_BE_CONFIRMED || retailOrder.getStatus() >= RetailOrder.COMPLETED) {
            String message = "当前订单状态不支持催单，只有待接单、已接单、配送中的订单才可以催单。";
            saveLog(userId, conversationId, "remindOrder", buildRequest("orderToken", orderToken), null, false, message);
            return message;
        }
        retailOrderService.reminder(retailOrder.getId());
        String message = "催单请求已提交，门店端会收到订单提醒。";
        saveLog(userId, conversationId, "remindOrder", buildRequest("orderToken", orderToken), buildResponse(message), true, null);
        return message;
    }

    /**
     * 仅允许用户取消自己的待付款订单，降低误取消和退款风险
     */
    @Tool(name = "cancelUnpaidOrderById", description = "根据订单ID取消当前用户自己的待付款订单，已支付或履约中的订单禁止通过该工具直接取消")
    public String cancelUnpaidOrder(Long userId, Long conversationId, Long orderId) throws Exception {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderId);
        if (!RetailOrder.PENDING_PAYMENT.equals(retailOrder.getStatus())) {
            String message = "只有待付款订单才允许通过 AI 直接取消。";
            saveLog(userId, conversationId, "cancelUnpaidOrder", buildRequest("orderId", orderId), null, false, message);
            return message;
        }
        retailOrderService.userCancelById(orderId);
        String message = "待付款订单已取消。";
        saveLog(userId, conversationId, "cancelUnpaidOrder", buildRequest("orderId", orderId), buildResponse(message), true, null);
        return message;
    }

    /**
     * 同时支持订单号和订单ID取消待付款订单。
     */
    @Tool(name = "cancelUnpaidOrderByToken", description = "根据订单号取消当前用户自己的待付款订单，已支付或履约中的订单禁止通过该工具直接取消")
    public String cancelUnpaidOrder(Long userId, Long conversationId, String orderToken) throws Exception {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderToken);
        if (!RetailOrder.PENDING_PAYMENT.equals(retailOrder.getStatus())) {
            String message = "只有待付款订单才允许通过 AI 直接取消。";
            saveLog(userId, conversationId, "cancelUnpaidOrder", buildRequest("orderToken", orderToken), null, false, message);
            return message;
        }
        retailOrderService.userCancelById(retailOrder.getId());
        String message = "待付款订单已取消。";
        saveLog(userId, conversationId, "cancelUnpaidOrder", buildRequest("orderToken", orderToken), buildResponse(message), true, null);
        return message;
    }

    /**
     * 先记录售后申请，再交由人工或后续自动化流程处理
     */
    @Tool(name = "createAfterSaleRequestById", description = "根据订单ID为当前用户自己的订单提交售后申请，需要提供售后原因和补充说明")
    public String createAfterSaleRequest(Long userId, Long conversationId, Long orderId, String reason, String description) {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderId);
        if (retailOrder.getStatus() == null || retailOrder.getStatus() < RetailOrder.TO_BE_CONFIRMED) {
            String message = "订单尚未生成有效履约记录，暂不支持发起售后申请。";
            saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleRequest(orderId, reason, description), null, false, message);
            return message;
        }
        if (!StringUtils.hasText(reason)) {
            String message = "售后原因不能为空。";
            saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleRequest(orderId, reason, description), null, false, message);
            return message;
        }

        AiAfterSaleRequest request = new AiAfterSaleRequest();
        request.setOrderId(orderId);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setRequestType("after_sale");
        request.setReason(reason.trim());
        request.setDescription(StringUtils.hasText(description) ? description.trim() : null);
        request.setStatus(0);
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        aiAfterSaleRequestMapper.insert(request);

        String message = "售后申请已提交，客服会根据订单和申请原因继续处理。";
        saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleRequest(orderId, reason, description), request, true, null);
        return message;
    }

    /**
     * 同时支持订单号和订单ID提交售后申请。
     */
    @Tool(name = "createAfterSaleRequestByToken", description = "根据订单号为当前用户自己的订单提交售后申请，需要提供售后原因和补充说明")
    public String createAfterSaleRequest(Long userId, Long conversationId, String orderToken, String reason, String description) {
        RetailOrder retailOrder = requireOwnedOrder(userId, orderToken);
        if (retailOrder.getStatus() == null || retailOrder.getStatus() < RetailOrder.TO_BE_CONFIRMED) {
            String message = "订单尚未生成有效履约记录，暂不支持发起售后申请。";
            saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleTokenRequest(orderToken, reason, description), null, false, message);
            return message;
        }
        if (!StringUtils.hasText(reason)) {
            String message = "售后原因不能为空。";
            saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleTokenRequest(orderToken, reason, description), null, false, message);
            return message;
        }

        AiAfterSaleRequest request = new AiAfterSaleRequest();
        request.setOrderId(retailOrder.getId());
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setRequestType("after_sale");
        request.setReason(reason.trim());
        request.setDescription(StringUtils.hasText(description) ? description.trim() : null);
        request.setStatus(0);
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        aiAfterSaleRequestMapper.insert(request);

        String message = "售后申请已提交，客服会根据订单和申请原因继续处理。";
        saveLog(userId, conversationId, "createAfterSaleRequest", buildAfterSaleTokenRequest(orderToken, reason, description), request, true, null);
        return message;
    }

    private OrderStatusVO toOrderStatusVO(RetailOrder retailOrder) {
        OrderStatusVO vo = new OrderStatusVO();
        BeanUtils.copyProperties(retailOrder, vo);
        vo.setOrderId(retailOrder.getId());
        vo.setOrderNumber(retailOrder.getNumber());
        vo.setStatusLabel(statusLabel(retailOrder.getStatus()));
        vo.setPayStatusLabel(payStatusLabel(retailOrder.getPayStatus()));
        return vo;
    }

    private String statusLabel(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "待付款";
            case 2:
                return "待接单";
            case 3:
                return "已接单";
            case 4:
                return "配送中";
            case 5:
                return "已完成";
            case 6:
                return "已取消";
            default:
                return "未知";
        }
    }

    private String payStatusLabel(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
            case 2:
                return "已退款";
            default:
                return "未知";
        }
    }

    /**
     * 校验订单归属，所有写操作必须先过这一层
     */
    private RetailOrder requireOwnedOrder(Long userId, Long orderId) {
        RetailOrder retailOrder = retailOrderMapper.getById(orderId);
        if (retailOrder == null || userId == null || !userId.equals(retailOrder.getUserId())) {
            throw new IllegalArgumentException("订单不存在或无权限访问");
        }
        return retailOrder;
    }

    /**
     * 优先按业务订单号匹配，再降级按主键ID匹配，兼容用户自然输入。
     */
    private RetailOrder requireOwnedOrder(Long userId, String orderToken) {
        RetailOrder retailOrder = resolveOwnedOrder(userId, orderToken);
        if (retailOrder == null) {
            throw new IllegalArgumentException("订单不存在或无权限访问");
        }
        return retailOrder;
    }

    private RetailOrder resolveOwnedOrder(Long userId, String orderToken) {
        if (userId == null || !StringUtils.hasText(orderToken)) {
            return null;
        }
        RetailOrder retailOrder = retailOrderMapper.getByNumberAndUserId(orderToken, userId);
        if (retailOrder != null) {
            return retailOrder;
        }
        try {
            RetailOrder byId = retailOrderMapper.getById(Long.valueOf(orderToken));
            if (byId != null && userId.equals(byId.getUserId())) {
                return byId;
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    private Map<String, Object> buildAfterSaleRequest(Long orderId, String reason, String description) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("orderId", orderId);
        request.put("reason", reason);
        request.put("description", description);
        return request;
    }

    private Map<String, Object> buildAfterSaleTokenRequest(String orderToken, String reason, String description) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("orderToken", orderToken);
        request.put("reason", reason);
        request.put("description", description);
        return request;
    }

    private Map<String, Object> buildRequest(String key, Object value) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put(key, value);
        return request;
    }

    private Map<String, Object> buildResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return response;
    }

    private void saveLog(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage) {
        customerAiToolLogService.saveToolCall(userId, conversationId, toolName, request, response, success, errorMessage);
    }
}
