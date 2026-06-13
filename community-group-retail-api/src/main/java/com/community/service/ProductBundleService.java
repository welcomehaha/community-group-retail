package com.community.service;

import com.community.dto.ProductBundleDTO;
import com.community.dto.ProductBundlePageQueryDTO;
import com.community.entity.ProductBundle;
import com.community.result.PageResult;
import com.community.vo.ProductItemVO;
import com.community.vo.ProductBundleVO;

import java.util.List;

public interface ProductBundleService {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param productBundleDTO
     */
    void saveWithProduct(ProductBundleDTO productBundleDTO);

    /**
     * 分页查询
     *
     * @param productBundlePageQueryDTO
     * @return
     */
    PageResult pageQuery(ProductBundlePageQueryDTO productBundlePageQueryDTO);

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐和关联的菜品数据
     *
     * @param id
     * @return
     */
    ProductBundleVO getByIdWithProduct(Long id);

    /**
     * 修改套餐
     *
     * @param productBundleDTO
     */
    void update(ProductBundleDTO productBundleDTO);

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<ProductBundle> list(ProductBundle setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<ProductItemVO> getProductItemById(Long id);
}
