package com.community.ai.tool;

import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AddressBook;
import com.community.entity.RetailOrder;
import com.community.mapper.AddressBookMapper;
import com.community.mapper.RetailOrderMapper;
import com.community.vo.UserDeliveryProfileVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户画像工具，聚合默认地址和最近订单地址，给配送/门店场景提供上下文。
 */
@Component
public class UserProfileTools {

    private final AddressBookMapper addressBookMapper;
    private final RetailOrderMapper retailOrderMapper;
    private final CustomerAiToolLogService customerAiToolLogService;

    public UserProfileTools(AddressBookMapper addressBookMapper,
                            RetailOrderMapper retailOrderMapper,
                            CustomerAiToolLogService customerAiToolLogService) {
        this.addressBookMapper = addressBookMapper;
        this.retailOrderMapper = retailOrderMapper;
        this.customerAiToolLogService = customerAiToolLogService;
    }

    /**
     * 查询用户配送画像，供 AI 回答配送范围、门店服务区域、默认收货信息等问题。
     */
    @Tool(description = "查询当前用户默认收货地址和最近订单地址，用于回答配送范围、收货信息、门店服务区域等问题")
    public UserDeliveryProfileVO queryUserDeliveryProfile(Long userId, Long conversationId) {
        UserDeliveryProfileVO profile = new UserDeliveryProfileVO();

        AddressBook query = AddressBook.builder()
                .userId(userId)
                .isDefault(1)
                .build();
        List<AddressBook> addressBooks = addressBookMapper.list(query);
        if (addressBooks != null && !addressBooks.isEmpty()) {
            AddressBook addressBook = addressBooks.get(0);
            profile.setDefaultConsignee(addressBook.getConsignee());
            profile.setDefaultPhone(addressBook.getPhone());
            profile.setDefaultAddress(buildAddress(addressBook));
        }

        List<RetailOrder> recentRetailOrder = retailOrderMapper.listRecentByUserId(userId);
        if (recentRetailOrder != null && !recentRetailOrder.isEmpty()) {
            RetailOrder recentOrder = recentRetailOrder.get(0);
            profile.setRecentOrderAddress(recentOrder.getAddress());
            profile.setRecentOrderConsignee(recentOrder.getConsignee());
            profile.setRecentOrderPhone(recentOrder.getPhone());
        }

        boolean success = StringUtils.hasText(profile.getDefaultAddress()) || StringUtils.hasText(profile.getRecentOrderAddress());
        saveLog(userId, conversationId, "queryUserDeliveryProfile", buildRequest(userId), profile, success,
                success ? null : "未查询到默认地址或最近订单地址");
        return success ? profile : null;
    }

    private String buildAddress(AddressBook addressBook) {
        StringBuilder address = new StringBuilder();
        if (StringUtils.hasText(addressBook.getProvinceName())) {
            address.append(addressBook.getProvinceName());
        }
        if (StringUtils.hasText(addressBook.getCityName())) {
            address.append(addressBook.getCityName());
        }
        if (StringUtils.hasText(addressBook.getDistrictName())) {
            address.append(addressBook.getDistrictName());
        }
        if (StringUtils.hasText(addressBook.getDetail())) {
            address.append(addressBook.getDetail());
        }
        return address.toString();
    }

    private Map<String, Object> buildRequest(Long userId) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("userId", userId);
        return request;
    }

    private void saveLog(Long userId, Long conversationId, String toolName, Object request, Object response, boolean success, String errorMessage) {
        customerAiToolLogService.saveToolCall(userId, conversationId, toolName, request, response, success, errorMessage);
    }
}
