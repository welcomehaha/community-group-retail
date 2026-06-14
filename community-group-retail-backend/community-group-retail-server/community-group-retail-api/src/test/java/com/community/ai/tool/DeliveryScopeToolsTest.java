package com.community.ai.tool;

import com.community.ai.service.CustomerAiToolLogService;
import com.community.vo.DeliveryScopeCheckVO;
import com.community.vo.UserDeliveryProfileVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryScopeToolsTest {

    @Mock
    private UserProfileTools userProfileTools;

    @Mock
    private CustomerAiToolLogService customerAiToolLogService;

    @InjectMocks
    private DeliveryScopeTools deliveryScopeTools;

    @Test
    void shouldReturnUnknownWhenMapConfigMissing() {
        UserDeliveryProfileVO profile = new UserDeliveryProfileVO();
        profile.setDefaultAddress("浙江省杭州市西湖区文三路 1 号");

        when(userProfileTools.queryUserDeliveryProfile(1001L, 2001L)).thenReturn(profile);
        ReflectionTestUtils.setField(deliveryScopeTools, "shopAddress", "北京市海淀区上地十街10号");
        ReflectionTestUtils.setField(deliveryScopeTools, "baiduAk", "your-ak");

        DeliveryScopeCheckVO result = deliveryScopeTools.checkDeliveryScope(1001L, 2001L);

        assertNotNull(result);
        assertEquals("unknown", result.getScopeStatus());
        verify(customerAiToolLogService).saveToolCall(any(), any(), any(), any(), any(), any(boolean.class), any());
    }
}
