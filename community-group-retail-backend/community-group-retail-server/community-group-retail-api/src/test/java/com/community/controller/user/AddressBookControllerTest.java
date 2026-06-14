package com.community.controller.user;

import com.community.context.BaseContext;
import com.community.exception.AddressBookBusinessException;
import com.community.handler.GlobalExceptionHandler;
import com.community.service.AddressBookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AddressBookControllerTest {

    @Mock
    private AddressBookService addressBookService;

    @InjectMocks
    private AddressBookController addressBookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BaseContext.setCurrentId(1001L);
        mockMvc = MockMvcBuilders.standaloneSetup(addressBookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void shouldRejectSaveWhenPhoneIsInvalid() throws Exception {
        String requestBody = """
                {
                  "consignee": "张三",
                  "phone": "123456",
                  "sex": "1",
                  "provinceCode": "110000",
                  "provinceName": "北京市",
                  "cityCode": "110100",
                  "cityName": "北京市",
                  "districtCode": "110101",
                  "districtName": "东城区",
                  "detail": "建国门内大街 1 号",
                  "label": "1"
                }
                """;

        mockMvc.perform(post("/user/addressBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("手机号输入有误"));

        verify(addressBookService, never()).save(any());
    }

    @Test
    void shouldRejectUpdateWhenIdMissing() throws Exception {
        String requestBody = """
                {
                  "consignee": "张三",
                  "phone": "13812345678",
                  "sex": "1",
                  "provinceCode": "110000",
                  "provinceName": "北京市",
                  "cityCode": "110100",
                  "cityName": "北京市",
                  "districtCode": "110101",
                  "districtName": "东城区",
                  "detail": "朝阳门南大街 8 号",
                  "label": "2"
                }
                """;

        mockMvc.perform(put("/user/addressBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("地址ID不能为空"));

        verify(addressBookService, never()).update(any());
    }

    @Test
    void shouldRejectDeleteWhenIdMissing() throws Exception {
        mockMvc.perform(delete("/user/addressBook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("地址ID不能为空"));
    }

    @Test
    void shouldReturnBusinessErrorWhenDeleteAddressForbidden() throws Exception {
        doThrow(new AddressBookBusinessException("地址不存在或无权限访问"))
                .when(addressBookService).deleteById(20L);

        mockMvc.perform(delete("/user/addressBook").param("id", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("地址不存在或无权限访问"));
    }

    @Test
    void shouldReturnBusinessErrorWhenSetDefaultForbidden() throws Exception {
        doThrow(new AddressBookBusinessException("地址不存在或无权限访问"))
                .when(addressBookService).setDefault(any());

        String requestBody = """
                {
                  "id": 20
                }
                """;

        mockMvc.perform(put("/user/addressBook/default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("地址不存在或无权限访问"));
    }
}
