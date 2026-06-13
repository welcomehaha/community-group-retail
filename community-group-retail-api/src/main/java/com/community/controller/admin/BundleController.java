package com.community.controller.admin;

import com.community.dto.ProductBundleDTO;
import com.community.dto.ProductBundlePageQueryDTO;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.ProductBundleService;
import com.community.vo.ProductBundleVO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组合商品管理
 */
@RestController
@RequestMapping({"/admin/bundle", "/admin/setmeal"})
@Tag(name = "组合商品相关接口")
@Slf4j
public class BundleController {

    @Autowired
    private ProductBundleService productBundleService;

    /**
     * 新增组合商品
     *
     * @param productBundleDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增组合商品")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:add')")
    @CacheEvict(cacheNames = "productBundleCache",key = "#productBundleDTO.categoryId")//key: productBundleCache::100
    public Result save(@Valid @RequestBody ProductBundleDTO productBundleDTO) {
        productBundleService.saveWithProduct(productBundleDTO);
        return Result.success();
    }

    /**
     * 组合商品分页查询
     *
     * @param productBundlePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "组合商品分页查询")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:list')")
    public Result<PageResult> page(ProductBundlePageQueryDTO productBundlePageQueryDTO) {
        PageResult pageResult = productBundleService.pageQuery(productBundlePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除组合商品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(summary = "批量删除组合商品")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:delete')")
    @CacheEvict(cacheNames = "productBundleCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        productBundleService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询组合商品，用于修改页面回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询组合商品")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:detail')")
    public Result<ProductBundleVO> getById(@PathVariable Long id) {
        ProductBundleVO productBundleVO = productBundleService.getByIdWithProduct(id);
        return Result.success(productBundleVO);
    }

    /**
     * 修改组合商品
     *
     * @param productBundleDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改组合商品")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:edit')")
    @CacheEvict(cacheNames = "productBundleCache",allEntries = true)
    public Result update(@Valid @RequestBody ProductBundleDTO productBundleDTO) {
        productBundleService.update(productBundleDTO);
        return Result.success();
    }

    /**
     * 组合商品上架下架
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "组合商品上架下架")
    @PreAuthorize("@permissionService.hasAuthority('bundle:item:onoff')")
    @CacheEvict(cacheNames = "productBundleCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        productBundleService.startOrStop(status, id);
        return Result.success();
    }
}


