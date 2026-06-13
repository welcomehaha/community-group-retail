package com.community.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.constant.MessageConstant;
import com.community.constant.StatusConstant;
import com.community.context.BaseContext;
import com.community.dto.ProductCategoryDTO;
import com.community.dto.ProductCategoryPageQueryDTO;
import com.community.entity.ProductCategory;
import com.community.exception.DeletionNotAllowedException;
import com.community.mapper.ProductCategoryMapper;
import com.community.mapper.ProductMapper;
import com.community.mapper.ProductBundleMapper;
import com.community.result.PageResult;
import com.community.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层
 */
@Service
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductBundleMapper productBundleMapper;

    /**
     * 新增分类
     * @param productCategoryDTO
     */
    public void save(ProductCategoryDTO productCategoryDTO) {
        ProductCategory productCategory = new ProductCategory();
        //属性拷贝
        BeanUtils.copyProperties(productCategoryDTO, productCategory);

        //分类状态默认为禁用状态0
        productCategory.setStatus(StatusConstant.DISABLE);

        //设置创建时间、修改时间、创建人、修改人
        //productCategory.setCreateTime(LocalDateTime.now());
        //productCategory.setUpdateTime(LocalDateTime.now());
        //productCategory.setCreateUser(BaseContext.getCurrentId());
        //productCategory.setUpdateUser(BaseContext.getCurrentId());

        productCategoryMapper.insert(productCategory);
    }

    /**
     * 分页查询
     * @param productCategoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(ProductCategoryPageQueryDTO productCategoryPageQueryDTO) {
        PageHelper.startPage(productCategoryPageQueryDTO.getPage(),productCategoryPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<ProductCategory> page = productCategoryMapper.pageQuery(productCategoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除分类
     * @param id
     */
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = productMapper.countByProductCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = productBundleMapper.countByProductCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        productCategoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     * @param productCategoryDTO
     */
    public void update(ProductCategoryDTO productCategoryDTO) {
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryDTO,productCategory);

        //设置修改时间、修改人
        //productCategory.setUpdateTime(LocalDateTime.now());
        //productCategory.setUpdateUser(BaseContext.getCurrentId());

        productCategoryMapper.update(productCategory);
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        ProductCategory productCategory = ProductCategory.builder()
                .id(id)
                .status(status)
                //.updateTime(LocalDateTime.now())
                //.updateUser(BaseContext.getCurrentId())
                .build();
        productCategoryMapper.update(productCategory);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<ProductCategory> list(Integer type) {
        return productCategoryMapper.list(type);
    }
}
