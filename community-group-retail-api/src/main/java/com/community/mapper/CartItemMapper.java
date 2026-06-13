package com.community.mapper;

import com.community.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CartItemMapper {

    /**
     * 动态条件查询
     * @param cartItem
     * @return
     */
    List<CartItem> list(CartItem cartItem);

    /**
     * 根据id修改商品数量
     * @param cartItem
     */
    @Update("update cart set num = #{number} where id = #{id}")
    void updateNumberById(CartItem cartItem);

    /**
     * 插入购物车数据
     * @param cartItem
     */
    @Insert("insert into cart (name, uid, prod_id, pack_id, spec, num, amount, image, create_time) " +
            " values (#{name},#{userId},#{productId},#{productBundleId},#{productSpec},#{number},#{amount},#{image},#{createTime})")
    void insert(CartItem cartItem);

    /**
     * 根据用户id删除购物车数据
     * @param userId
     */
    @Delete("delete from cart where uid = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车数据
     * @param id
     */
    @Delete("delete from cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量插入购物车数据
     *
     * @param cartItemList
     */
    void insertBatch(List<CartItem> cartItemList);
}
