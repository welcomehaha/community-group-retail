package com.community.controller.user;

import com.community.entity.ProductCategory;
import com.community.result.Result;
import com.community.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userProductCategoryController")
@RequestMapping("/user/category")
@Tag(name = "C端-分类接口")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询分类")
    public Result<List<ProductCategory>> list(Integer type) {
        List<ProductCategory> list = productCategoryService.list(type);
        return Result.success(list);
    }
}


