package com.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetailOrderCancelDTO implements Serializable {

    private Long id;
    //订单取消原因
    private String cancelReason;

}
