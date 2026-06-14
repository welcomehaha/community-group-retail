package com.community.controller.admin;

import com.community.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("adminStoreController")
@RequestMapping({"/admin/store", "/admin/shop"})
@Tag(name = "门店相关接口")
@Slf4j
public class StoreController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置门店的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @Operation(summary = "设置门店的营业状态")
    @PreAuthorize("@permissionService.hasAuthority('store:status:edit')")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置门店的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 获取门店的营业状态
     * @return
     */
    @GetMapping("/status")
    @Operation(summary = "获取门店的营业状态")
    @PreAuthorize("@permissionService.hasAuthority('store:status:view')")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            status = 0;
            redisTemplate.opsForValue().set(KEY, status);
        }
        log.info("获取到门店的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}


