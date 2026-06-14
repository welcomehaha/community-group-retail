package com.community.ai.tool;

import com.community.ai.service.CustomerAiToolLogService;
import com.community.entity.AddressBook;
import com.community.entity.RetailOrder;
import com.community.mapper.AddressBookMapper;
import com.community.mapper.RetailOrderMapper;
import com.community.vo.UserDeliveryProfileVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileToolsTest {

    @Mock
    private AddressBookMapper addressBookMapper;

    @Mock
    private RetailOrderMapper retailOrderMapper;

    @Mock
    private CustomerAiToolLogService customerAiToolLogService;

    @InjectMocks
    private UserProfileTools userProfileTools;

    @Test
    void shouldBuildDeliveryProfileFromDefaultAddressAndRecentOrder() {
        AddressBook addressBook = AddressBook.builder()
                .userId(1001L)
                .consignee("张三")
                .phone("13800000000")
                .provinceName("浙江省")
                .cityName("杭州市")
                .districtName("西湖区")
                .detail("文三路 1 号")
                .isDefault(1)
                .build();

        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setAddress("浙江省杭州市西湖区文二路 2 号");
        retailOrder.setConsignee("张三");
        retailOrder.setPhone("13800000000");

        when(addressBookMapper.list(any(AddressBook.class))).thenReturn(Collections.singletonList(addressBook));
        when(retailOrderMapper.listRecentByUserId(1001L)).thenReturn(Collections.singletonList(retailOrder));

        UserDeliveryProfileVO result = userProfileTools.queryUserDeliveryProfile(1001L, 2001L);

        assertNotNull(result);
        assertEquals("浙江省杭州市西湖区文三路 1 号", result.getDefaultAddress());
        assertEquals("浙江省杭州市西湖区文二路 2 号", result.getRecentOrderAddress());
        verify(customerAiToolLogService).saveToolCall(any(), any(), any(), any(), any(), any(boolean.class), any());
    }
}
