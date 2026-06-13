package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginDTO implements Serializable {

    /**
     * 微信登录临时凭证。
     */
    @NotBlank(message = "微信登录凭证不能为空")
    private String code;

}
