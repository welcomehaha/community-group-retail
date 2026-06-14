package com.community.controller.user;

import com.community.constant.StatusConstant;
import com.community.entity.Product;
import com.community.result.Result;
import com.community.service.ProductService;
import com.community.vo.ProductVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userProductController")
@RequestMapping({"/user/product", "/user/dish"})
@Slf4j
@Tag(name = "C端-商品浏览接口")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询商品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询商品")
    public Result<List<ProductVO>> list(Long categoryId) {

        //构造redis中的key，规则：dish_分类id
        String key = "dish_" + categoryId;

        //查询redis中是否存在商品数据
        List<ProductVO> list = (List<ProductVO>) redisTemplate.opsForValue().get(key);
        if(list != null && list.size() > 0){
            //如果存在，直接返回，无须查询数据库
            return Result.success(list);
        }

        Product dish = new Product();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);// 查询上架中的商品

        //如果不存在，查询数据库，将查询到的数据放入redis中
        list = productService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}


