package com.community.service;

import com.community.dto.ProductCategoryDTO;
import com.community.dto.ProductCategoryPageQueryDTO;
import com.community.entity.ProductCategory;
import com.community.result.PageResult;
import java.util.List;

public interface ProductCategoryService {

    /**
     * 新增分类
     * @param productCategoryDTO
     */
    void save(ProductCategoryDTO productCategoryDTO);

    /**
     * 分页查询
     * @param productCategoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(ProductCategoryPageQueryDTO productCategoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteById(Long id);

    /**
     * 修改分类
     * @param productCategoryDTO
     */
    void update(ProductCategoryDTO productCategoryDTO);

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<ProductCategory> list(Integer type);
}
