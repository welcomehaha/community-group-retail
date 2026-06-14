package com.community.controller.user;

import com.community.constant.StatusConstant;
import com.community.entity.ProductBundle;
import com.community.result.Result;
import com.community.service.ProductBundleService;
import com.community.vo.ProductItemVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userBundleController")
@RequestMapping({"/user/bundle", "/user/setmeal"})
@Tag(name = "C端-组合商品浏览接口")
public class BundleController {
    @Autowired
    private ProductBundleService productBundleService;

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询组合商品")
    @Cacheable(cacheNames = "productBundleCache",key = "#categoryId") //key: productBundleCache::100
    public Result<List<ProductBundle>> list(Long categoryId) {
        ProductBundle setmeal = new ProductBundle();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        List<ProductBundle> list = productBundleService.list(setmeal);
        return Result.success(list);
    }

    /**
     * 根据组合商品id查询包含的商品列表
     *
     * @param id
     * @return
     */
    @GetMapping({"/product/{id}", "/dish/{id}"})
    @Operation(summary = "根据组合商品id查询包含的商品列表")
    public Result<List<ProductItemVO>> productList(@PathVariable("id") Long id) {
        List<ProductItemVO> list = productBundleService.getProductItemById(id);
        return Result.success(list);
    }
}


