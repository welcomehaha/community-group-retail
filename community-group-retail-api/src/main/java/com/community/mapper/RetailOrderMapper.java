package com.community.mapper;

import com.github.pagehelper.Page;
import com.community.dto.GoodsSalesDTO;
import com.community.dto.RetailOrderPageQueryDTO;
import com.community.entity.RetailOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface RetailOrderMapper {

    /**
     * 插入订单数据
     * @param retailOrder
     */
    void insert(RetailOrder retailOrder);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select id, no as number, status, uid as user_id, addr_id as address_book_id, ord_time as order_time, " +
            "checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, " +
            "rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, " +
            "tableware_number, tableware_status from ord where no = #{orderNumber} and uid = #{userId}")
    RetailOrder getByNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    /**
     * 修改订单信息
     * @param retailOrder
     */
    void update(RetailOrder retailOrder);

    /**
     * 分页条件查询并按下单时间排序
     * @param retailOrderPageQueryDTO
     */
    Page<RetailOrder> pageQuery(RetailOrderPageQueryDTO retailOrderPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select id, no as number, status, uid as user_id, addr_id as address_book_id, ord_time as order_time, " +
            "checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, " +
            "rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, " +
            "tableware_number, tableware_status from ord where id = #{id}")
    RetailOrder getById(Long id);

    @Select("select id, no as number, status, uid as user_id, addr_id as address_book_id, ord_time as order_time, " +
            "checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, " +
            "rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, " +
            "tableware_number, tableware_status from ord where uid = #{userId} order by ord_time desc limit 10")
    List<RetailOrder> listRecentByUserId(Long userId);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from ord where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select id, no as number, status, uid as user_id, addr_id as address_book_id, ord_time as order_time, " +
            "checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, " +
            "rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, " +
            "tableware_number, tableware_status from ord where status = #{status} and ord_time < #{orderTime}")
    List<RetailOrder> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
