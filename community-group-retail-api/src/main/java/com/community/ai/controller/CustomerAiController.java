package com.community.ai.controller;

import com.community.ai.service.CustomerAiConversationService;
import com.community.ai.service.CustomerAiService;
import com.community.context.BaseContext;
import com.community.dto.AiChatDTO;
import com.community.dto.AiCreateSessionDTO;
import com.community.result.Result;
import com.community.vo.AiChatVO;
import com.community.vo.AiConversationVO;
import com.community.vo.AiMessageVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/ai/customer")
@Tag(name = "用户端-客服AI接口")
public class CustomerAiController {

    private final CustomerAiService customerAiService;
    private final CustomerAiConversationService customerAiConversationService;

    public CustomerAiController(CustomerAiService customerAiService,
                                CustomerAiConversationService customerAiConversationService) {
        this.customerAiService = customerAiService;
        this.customerAiConversationService = customerAiConversationService;
    }

    @PostMapping("/session")
    @Operation(summary = "新建客服AI会话")
    public Result<Map<String, Long>> createSession(@RequestBody(required = false) AiCreateSessionDTO dto) {
        Long userId = BaseContext.getCurrentId();
        Long conversationId = customerAiConversationService.createConversation(userId, dto == null ? null : dto.getTitle());
        Map<String, Long> data = new HashMap<>();
        data.put("conversationId", conversationId);
        return Result.success(data);
    }

    @PostMapping("/chat")
    @Operation(summary = "发送客服AI消息")
    public Result<AiChatVO> chat(@RequestBody AiChatDTO dto) {
        if (dto == null || dto.getConversationId() == null || !StringUtils.hasText(dto.getMessage())) {
            return Result.error("conversationId 和 message 不能为空");
        }
        Long userId = BaseContext.getCurrentId();
        return Result.success(customerAiService.chat(userId, dto.getConversationId(), dto.getMessage()));
    }

    @GetMapping("/history")
    @Operation(summary = "查询会话列表")
    public Result<List<AiConversationVO>> history() {
        Long userId = BaseContext.getCurrentId();
        return Result.success(customerAiConversationService.listConversations(userId));
    }

    @GetMapping("/history/{conversationId}")
    @Operation(summary = "查询会话消息")
    public Result<List<AiMessageVO>> historyDetail(@PathVariable Long conversationId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(customerAiConversationService.listMessages(userId, conversationId));
    }

    @DeleteMapping("/history/{conversationId}")
    @Operation(summary = "删除会话")
    public Result<Void> delete(@PathVariable Long conversationId) {
        Long userId = BaseContext.getCurrentId();
        customerAiConversationService.deleteConversation(userId, conversationId);
        return Result.success();
    }
}


