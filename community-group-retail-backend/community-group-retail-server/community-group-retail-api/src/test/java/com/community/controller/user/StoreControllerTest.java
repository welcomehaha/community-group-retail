package com.community.controller.user;

import com.community.constant.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @InjectMocks
    private StoreController storeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        mockMvc = MockMvcBuilders.standaloneSetup(storeController).build();
    }

    @Test
    void shouldReturnDefaultStatusForStoreRouteWhenRedisValueMissing() throws Exception {
        when(valueOperations.get(StoreController.KEY)).thenReturn(null);

        mockMvc.perform(get("/user/store/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.DISABLE));

        verify(valueOperations).set(StoreController.KEY, StatusConstant.DISABLE);
    }

    @Test
    void shouldReturnDefaultStatusForLegacyShopRouteWhenRedisValueMissing() throws Exception {
        when(valueOperations.get(StoreController.KEY)).thenReturn(null);

        mockMvc.perform(get("/user/shop/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.DISABLE));

        verify(valueOperations).set(StoreController.KEY, StatusConstant.DISABLE);
    }

    @Test
    void shouldReturnExistingStatusWhenRedisValuePresent() throws Exception {
        when(valueOperations.get(StoreController.KEY)).thenReturn(StatusConstant.ENABLE);

        mockMvc.perform(get("/user/store/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.ENABLE));
    }
}
