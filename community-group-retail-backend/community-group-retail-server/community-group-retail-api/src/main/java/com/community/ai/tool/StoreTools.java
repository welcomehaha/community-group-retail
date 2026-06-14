package com.community.ai.tool;

import com.community.ai.mapper.AiToolCallLogMapper;
import com.community.controller.user.StoreController;
import com.community.entity.AiToolCallLog;
import com.community.vo.StoreStatusVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StoreTools {

    private final RedisTemplate redisTemplate;
    private final AiToolCallLogMapper aiToolCallLogMapper;

    public StoreTools(RedisTemplate redisTemplate, AiToolCallLogMapper aiToolCallLogMapper) {
        this.redisTemplate = redisTemplate;
        this.aiToolCallLogMapper = aiToolCallLogMapper;
    }

    @Tool(description = "查询门店当前营业状态，返回营业中或打烊中以及对应说明")
    public StoreStatusVO queryStoreStatus(Long userId, Long conversationId) {
        Integer status = (Integer) redisTemplate.opsForValue().get(StoreController.KEY);
        if (status == null) {
            status = 0;
        }
        StoreStatusVO vo = new StoreStatusVO();
        vo.setStatus(status);
        vo.setStatusLabel(status == 1 ? "营业中" : "打烊中");
        vo.setMessage(status == 1 ? "门店当前可正常接单。" : "门店当前处于打烊状态。");
        saveLog(userId, conversationId, "queryStoreStatus", "{}", String.valueOf(status), true, null);
        return vo;
    }

    private void saveLog(Long userId, Long conversationId, String toolName, String request, String response, boolean success, String errorMessage) {
        AiToolCallLog log = new AiToolCallLog();
        log.setUserId(userId);
        log.setConversationId(conversationId);
        log.setToolName(toolName);
        log.setRequestJson(request);
        log.setResponseJson(response);
        log.setSuccessFlag(success ? 1 : 0);
        log.setErrorMessage(errorMessage);
        log.setCreateTime(LocalDateTime.now());
        aiToolCallLogMapper.insert(log);
    }
}
