package com.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetailOrderRejectionDTO implements Serializable {

    private Long id;

    //订单拒绝原因
    private String rejectionReason;

}
