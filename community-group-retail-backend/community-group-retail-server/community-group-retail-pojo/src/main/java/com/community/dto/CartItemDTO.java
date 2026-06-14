package com.community.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class CartItemDTO implements Serializable {

    private Long productId;
    private Long productBundleId;
    private String productSpec;

}
