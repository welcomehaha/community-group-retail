package com.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编辑收货地址请求对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddressBookUpdateDTO extends AddressBookSaveDTO {

    /**
     * 地址主键。
     */
    @NotNull(message = "地址ID不能为空")
    private Long id;
}
