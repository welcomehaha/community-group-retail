package com.community.controller.admin;

import com.community.dto.ProductDTO;
import com.community.dto.ProductPageQueryDTO;
import com.community.entity.Product;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.ProductService;
import com.community.vo.ProductVO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 商品管理
 */
@RestController
@RequestMapping({"/admin/product", "/admin/dish"})
@Tag(name = "商品相关接口")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增商品
     *
     * @param productDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增商品")
    @PreAuthorize("@permissionService.hasAuthority('product:item:add')")
    public Result save(@Valid @RequestBody ProductDTO productDTO) {
        log.info("新增商品：{}", productDTO);
        productService.saveWithFlavor(productDTO);

        //清理缓存数据
        String key = "dish_" + productDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 商品分页查询
     *
     * @param productPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "商品分页查询")
    @PreAuthorize("@permissionService.hasAuthority('product:item:list')")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO) {
        log.info("商品分页查询:{}", productPageQueryDTO);
        PageResult pageResult = productService.pageQuery(productPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 商品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(summary = "商品批量删除")
    @PreAuthorize("@permissionService.hasAuthority('product:item:delete')")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("商品批量删除：{}", ids);
        productService.deleteBatch(ids);

        // 清理商品缓存数据，兼容沿用 dish_ 前缀
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询商品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询商品")
    @PreAuthorize("@permissionService.hasAuthority('product:item:detail')")
    public Result<ProductVO> getById(@PathVariable Long id) {
        log.info("根据id查询商品：{}", id);
        ProductVO productVO = productService.getByIdWithFlavor(id);
        return Result.success(productVO);
    }

    /**
     * 修改商品
     *
     * @param productDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改商品")
    @PreAuthorize("@permissionService.hasAuthority('product:item:edit')")
    public Result update(@Valid @RequestBody ProductDTO productDTO) {
        log.info("修改商品：{}", productDTO);
        productService.updateWithFlavor(productDTO);

        // 清理商品缓存数据，兼容沿用 dish_ 前缀
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 商品上架下架
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "商品上架下架")
    @PreAuthorize("@permissionService.hasAuthority('product:item:onoff')")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        productService.startOrStop(status, id);

        // 清理商品缓存数据，兼容沿用 dish_ 前缀
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询商品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询商品")
    @PreAuthorize("@permissionService.hasAuthority('product:item:productCategory-list')")
    public Result<List<Product>> list(Long categoryId) {
        List<Product> list = productService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}


