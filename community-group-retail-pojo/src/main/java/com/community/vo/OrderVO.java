package com.community.vo;

import com.community.entity.RetailOrderItem;
import com.community.entity.RetailOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends RetailOrder implements Serializable {

    //订单菜品信息
    private String orderProductes;

    //订单详情
    private List<RetailOrderItem> orderDetailList;

}
