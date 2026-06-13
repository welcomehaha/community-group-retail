package com.community.ai.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.community.ai.service.CustomerAiToolLogService;
import com.community.utils.HttpClientUtil;
import com.community.vo.DeliveryScopeCheckVO;
import com.community.vo.UserDeliveryProfileVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配送范围工具，优先复用现有下单流程中的地图距离校验思路。
 */
@Component
public class DeliveryScopeTools {

    private static final int DELIVERY_DISTANCE_LIMIT_METERS = 5000;

    private final UserProfileTools userProfileTools;
    private final CustomerAiToolLogService customerAiToolLogService;

    @Value("${community.shop.address:}")
    private String shopAddress;

    @Value("${community.baidu.ak:}")
    private String baiduAk;

    public DeliveryScopeTools(UserProfileTools userProfileTools,
                              CustomerAiToolLogService customerAiToolLogService) {
        this.userProfileTools = userProfileTools;
        this.customerAiToolLogService = customerAiToolLogService;
    }

    /**
     * 根据用户默认地址或最近订单地址，尝试评估是否在门店配送范围内。
     */
    @Tool(description = "评估当前用户默认地址或最近订单地址是否可能处于门店配送范围内；若缺少地图配置或地址信息，则返回待人工确认")
    public DeliveryScopeCheckVO checkDeliveryScope(Long userId, Long conversationId) {
        UserDeliveryProfileVO profile = userProfileTools.queryUserDeliveryProfile(userId, conversationId);
        String targetAddress = resolveTargetAddress(profile);
        if (!StringUtils.hasText(targetAddress)) {
            DeliveryScopeCheckVO result = buildUnknownResult(null, "当前没有可用于判断配送范围的收货地址，请先维护默认地址或提供收货地址。");
            saveLog(userId, conversationId, "checkDeliveryScope", buildRequest(), result, false, result.getMessage());
            return result;
        }
        if (!StringUtils.hasText(shopAddress) || !StringUtils.hasText(baiduAk) || "your-ak".equalsIgnoreCase(baiduAk)) {
            DeliveryScopeCheckVO result = buildUnknownResult(targetAddress, "当前系统未完成地图服务配置，暂时无法自动判断配送范围，建议转人工或由下单页再次校验。");
            saveLog(userId, conversationId, "checkDeliveryScope", buildRequest(), result, false, result.getMessage());
            return result;
        }

        try {
            Integer distance = calculateDrivingDistance(shopAddress, targetAddress);
            DeliveryScopeCheckVO result = new DeliveryScopeCheckVO();
            result.setTargetAddress(targetAddress);
            result.setDistanceMeters(distance);
            if (distance != null && distance <= DELIVERY_DISTANCE_LIMIT_METERS) {
                result.setScopeStatus("supported");
                result.setMessage("按当前地图路线测算，收货地址大概率在门店配送范围内。实际结果以下单页校验为准。");
            } else {
                result.setScopeStatus("unsupported");
                result.setMessage("按当前地图路线测算，收货地址可能超出门店配送范围。实际结果以下单页校验或人工确认为准。");
            }
            saveLog(userId, conversationId, "checkDeliveryScope", buildRequest(), result, true, null);
            return result;
        } catch (Exception ex) {
            DeliveryScopeCheckVO result = buildUnknownResult(targetAddress, "配送范围自动评估失败，建议转人工或由下单页再次校验。");
            saveLog(userId, conversationId, "checkDeliveryScope", buildRequest(), result, false, ex.getMessage());
            return result;
        }
    }

    private String resolveTargetAddress(UserDeliveryProfileVO profile) {
        if (profile == null) {
            return null;
        }
        if (StringUtils.hasText(profile.getDefaultAddress())) {
            return profile.getDefaultAddress();
        }
        if (StringUtils.hasText(profile.getRecentOrderAddress())) {
            return profile.getRecentOrderAddress();
        }
        return null;
    }

    /**
     * 复用下单流程的地图距离计算逻辑，但在客服 AI 场景不抛业务异常，统一回传评估结果。
     */
    private Integer calculateDrivingDistance(String storeAddress, String userAddress) {
        String storeLngLat = geocodeAddress(storeAddress);
        String userLngLat = geocodeAddress(userAddress);

        Map<String, String> routeRequest = new LinkedHashMap<>();
        routeRequest.put("origin", storeLngLat);
        routeRequest.put("destination", userLngLat);
        routeRequest.put("steps_info", "0");
        routeRequest.put("ak", baiduAk);

        String routeResponse = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", routeRequest);
        JSONObject jsonObject = JSON.parseObject(routeResponse);
        if (jsonObject == null || !"0".equals(jsonObject.getString("status"))) {
            throw new IllegalStateException("配送路线规划失败");
        }

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray routes = result == null ? null : result.getJSONArray("routes");
        if (routes == null || routes.isEmpty()) {
            throw new IllegalStateException("配送路线结果为空");
        }

        Object distanceValue = routes.getJSONObject(0).get("distance");
        if (distanceValue == null) {
            throw new IllegalStateException("配送距离结果为空");
        }
        return Integer.valueOf(String.valueOf(distanceValue));
    }

    private String geocodeAddress(String address) {
        Map<String, String> geocodeRequest = new LinkedHashMap<>();
        geocodeRequest.put("address", address);
        geocodeRequest.put("output", "json");
        geocodeRequest.put("ak", baiduAk);

        String geocodeResponse = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", geocodeRequest);
        JSONObject jsonObject = JSON.parseObject(geocodeResponse);
        if (jsonObject == null || !"0".equals(jsonObject.getString("status"))) {
            throw new IllegalStateException("地址解析失败");
        }

        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        if (location == null) {
            throw new IllegalStateException("地址坐标为空");
        }
        return location.getString("lat") + "," + location.getString("lng");
    }

    private DeliveryScopeCheckVO buildUnknownResult(String targetAddress, String message) {
        DeliveryScopeCheckVO result = new DeliveryScopeCheckVO();
        result.setScopeStatus("unknown");
        result.setTargetAddress(targetAddress);
        result.setMessage(message);
        return result;
    }

    private Map<String, Object> buildRequest() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("shopAddress", shopAddress);
        request.put("baiduAkConfigured", StringUtils.hasText(baiduAk) && !"your-ak".equalsIgnoreCase(baiduAk));
        return request;
    }

    private void saveLog(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage) {
        customerAiToolLogService.saveToolCall(userId, conversationId, toolName, request, response, success, errorMessage);
    }
}
