package com.community.controller.user;

import com.community.constant.StatusConstant;
import com.community.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userStoreController")
@RequestMapping({"/user/store", "/user/shop"})
@Tag(name = "门店相关接口")
@Slf4j
public class StoreController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取门店的营业状态
     * @return
     */
    @GetMapping("/status")
    @Operation(summary = "获取门店的营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            status = StatusConstant.DISABLE;
            redisTemplate.opsForValue().set(KEY, status);
        }
        log.info("获取到门店的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}


