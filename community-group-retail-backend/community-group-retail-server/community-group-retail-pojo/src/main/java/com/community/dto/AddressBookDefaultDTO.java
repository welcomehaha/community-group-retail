package com.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 设置默认地址请求对象。
 */
@Data
public class AddressBookDefaultDTO implements Serializable {

    /**
     * 地址主键。
     */
    @NotNull(message = "默认地址ID不能为空")
    private Long id;
}
