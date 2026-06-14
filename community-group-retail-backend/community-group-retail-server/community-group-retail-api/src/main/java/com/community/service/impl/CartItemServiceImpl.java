package com.community.service.impl;

import com.community.context.BaseContext;
import com.community.dto.CartItemDTO;
import com.community.entity.Product;
import com.community.entity.ProductBundle;
import com.community.entity.CartItem;
import com.community.mapper.ProductMapper;
import com.community.mapper.ProductBundleMapper;
import com.community.mapper.CartItemMapper;
import com.community.service.CartItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductBundleMapper productBundleMapper;

    /**
     * 添加购物车
     * @param cartItemDTO
     */
    public void addCartItem(CartItemDTO cartItemDTO) {
        //判断当前加入到购物车中的商品是否已经存在了
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartItemDTO,cartItem);
        Long userId = BaseContext.getCurrentId();
        cartItem.setUserId(userId);

        List<CartItem> list = cartItemMapper.list(cartItem);

        //如果已经存在了，只需要将数量加一
        if(list != null && list.size() > 0){
            CartItem cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);//update shopping_cart set number = ? where id = ?
            cartItemMapper.updateNumberById(cart);
        }else {
            //如果不存在，需要插入一条购物车数据
            //判断本次添加到购物车的是菜品还是套餐
            Long productId = cartItemDTO.getProductId();
            if(productId != null){
                //本次添加到购物车的是菜品
                Product dish = productMapper.getById(productId);
                cartItem.setName(dish.getName());
                cartItem.setImage(dish.getImage());
                cartItem.setAmount(dish.getPrice());
            }else{
                //本次添加到购物车的是套餐
                Long productBundleId = cartItemDTO.getProductBundleId();
                ProductBundle setmeal = productBundleMapper.getById(productBundleId);
                cartItem.setName(setmeal.getName());
                cartItem.setImage(setmeal.getImage());
                cartItem.setAmount(setmeal.getPrice());
            }
            cartItem.setNumber(1);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItemMapper.insert(cartItem);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    public List<CartItem> showCartItem() {
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        CartItem cartItem = CartItem.builder()
                .userId(userId)
                .build();
        List<CartItem> list = cartItemMapper.list(cartItem);
        return list;
    }

    /**
     * 清空购物车
     */
    public void cleanCartItem() {
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        cartItemMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     * @param cartItemDTO
     */
    public void subCartItem(CartItemDTO cartItemDTO) {
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartItemDTO,cartItem);
        //设置查询条件，查询当前登录用户的购物车数据
        cartItem.setUserId(BaseContext.getCurrentId());

        List<CartItem> list = cartItemMapper.list(cartItem);

        if(list != null && list.size() > 0){
            cartItem = list.get(0);

            Integer number = cartItem.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                cartItemMapper.deleteById(cartItem.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                cartItem.setNumber(cartItem.getNumber() - 1);
                cartItemMapper.updateNumberById(cartItem);
            }
        }
    }
}
