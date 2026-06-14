package com.community.service;

import com.community.dto.CartItemDTO;
import com.community.entity.CartItem;

import java.util.List;

public interface CartItemService {

    /**
     * 添加购物车
     * @param cartItemDTO
     */
    void addCartItem(CartItemDTO cartItemDTO);

    /**
     * 查看购物车
     * @return
     */
    List<CartItem> showCartItem();

    /**
     * 清空购物车
     */
    void cleanCartItem();

    /**
     * 删除购物车中一个商品
     * @param cartItemDTO
     */
    void subCartItem(CartItemDTO cartItemDTO);
}
