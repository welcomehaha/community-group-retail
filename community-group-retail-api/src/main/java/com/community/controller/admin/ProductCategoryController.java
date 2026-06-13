package com.community.controller.admin;

import com.community.dto.ProductCategoryDTO;
import com.community.dto.ProductCategoryPageQueryDTO;
import com.community.entity.ProductCategory;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.ProductCategoryService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Tag(name = "分类相关接口")
@Slf4j
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 新增分类
     * @param productCategoryDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增分类")
    @PreAuthorize("@permissionService.hasAuthority('product:category:add')")
    public Result<String> save(@Valid @RequestBody ProductCategoryDTO productCategoryDTO){
        log.info("新增分类：{}", productCategoryDTO);
        productCategoryService.save(productCategoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     * @param productCategoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    @PreAuthorize("@permissionService.hasAuthority('product:category:list')")
    public Result<PageResult> page(ProductCategoryPageQueryDTO productCategoryPageQueryDTO){
        log.info("分页查询：{}", productCategoryPageQueryDTO);
        PageResult pageResult = productCategoryService.pageQuery(productCategoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @Operation(summary = "删除分类")
    @PreAuthorize("@permissionService.hasAuthority('product:category:delete')")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        productCategoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param productCategoryDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改分类")
    @PreAuthorize("@permissionService.hasAuthority('product:category:edit')")
    public Result<String> update(@Valid @RequestBody ProductCategoryDTO productCategoryDTO){
        productCategoryService.update(productCategoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用分类")
    @PreAuthorize("@permissionService.hasAuthority('product:category:status')")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        productCategoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    @PreAuthorize("@permissionService.hasAuthority('product:category:list')")
    public Result<List<ProductCategory>> list(Integer type){
        List<ProductCategory> list = productCategoryService.list(type);
        return Result.success(list);
    }
}


