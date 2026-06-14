package com.community.ai.tool;

import com.community.ai.mapper.AiToolCallLogMapper;
import com.community.entity.AiToolCallLog;
import com.community.entity.Product;
import com.community.mapper.ProductMapper;
import com.community.vo.ProductInfoVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductTools {

    private final ProductMapper productMapper;
    private final AiToolCallLogMapper aiToolCallLogMapper;

    public ProductTools(ProductMapper productMapper, AiToolCallLogMapper aiToolCallLogMapper) {
        this.productMapper = productMapper;
        this.aiToolCallLogMapper = aiToolCallLogMapper;
    }

    @Tool(description = "根据商品关键词查询商品名称、价格、描述和当前是否起售")
    public ProductInfoVO queryProductInfo(Long userId, Long conversationId, String keyword) {
        Product query = new Product();
        query.setName(keyword);
        List<Product> dishes = productMapper.list(query);
        if (dishes == null || dishes.isEmpty()) {
            saveLog(userId, conversationId, "queryProductInfo", keyword, "null", false, "product not found");
            return null;
        }

        Product dish = dishes.get(0);
        ProductInfoVO vo = new ProductInfoVO();
        vo.setProductId(dish.getId());
        vo.setProductName(dish.getName());
        vo.setPrice(dish.getPrice());
        vo.setDescription(dish.getDescription());
        vo.setStatus(dish.getStatus());
        vo.setStatusLabel(dish.getStatus() != null && dish.getStatus() == 1 ? "起售中" : "停售");
        vo.setAvailable(dish.getStatus() != null && dish.getStatus() == 1);
        saveLog(userId, conversationId, "queryProductInfo", keyword, String.valueOf(vo.getProductId()), true, null);
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
