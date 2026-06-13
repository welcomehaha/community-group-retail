package com.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNumber;

    private Integer status;

    private String statusLabel;

    private Integer payStatus;

    private String payStatusLabel;

    private BigDecimal amount;

    private LocalDateTime orderTime;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime deliveryTime;
}
