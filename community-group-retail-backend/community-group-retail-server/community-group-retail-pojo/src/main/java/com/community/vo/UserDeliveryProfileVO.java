package com.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户配送画像，用于客服 AI 感知默认地址和最近履约地址。
 */
@Data
public class UserDeliveryProfileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String defaultConsignee;

    private String defaultPhone;

    private String defaultAddress;

    private String recentOrderAddress;

    private String recentOrderConsignee;

    private String recentOrderPhone;
}
