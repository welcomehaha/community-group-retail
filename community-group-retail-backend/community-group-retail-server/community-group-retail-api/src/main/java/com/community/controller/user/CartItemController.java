package com.community.controller.user;

import com.community.dto.CartItemDTO;
import com.community.entity.CartItem;
import com.community.result.Result;
import com.community.service.CartItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "C端购物车相关接口")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    /**
     * 添加购物车
     * @param cartItemDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result add(@RequestBody CartItemDTO cartItemDTO){
        log.info("添加购物车，商品信息为：{}",cartItemDTO);
        cartItemService.addCartItem(cartItemDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查看购物车")
    public Result<List<CartItem>> list(){
        List<CartItem> list = cartItemService.showCartItem();
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result clean(){
        cartItemService.cleanCartItem();
        return Result.success();
    }

    /**
     * 删除购物车中一个商品
     * @param cartItemDTO
     * @return
     */
    @PostMapping("/sub")
    @Operation(summary = "删除购物车中一个商品")
    public Result sub(@RequestBody CartItemDTO cartItemDTO){
        log.info("删除购物车中一个商品，商品：{}", cartItemDTO);
        cartItemService.subCartItem(cartItemDTO);
        return Result.success();
    }
}


