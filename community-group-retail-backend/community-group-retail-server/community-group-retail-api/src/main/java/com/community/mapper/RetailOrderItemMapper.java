package com.community.mapper;

import com.community.entity.RetailOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RetailOrderItemMapper {
    /**
     * 批量插入订单明细数据
     * @param retailOrderItemList
     */
    void insertBatch(List<RetailOrderItem> retailOrderItemList);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select id, name, image, ord_id as order_id, prod_id as product_id, pack_id as product_bundle_id, spec as product_spec, " +
            "num as number, amount from ord_item where ord_id = #{orderId}")
    List<RetailOrderItem> getByOrderId(Long orderId);
}
