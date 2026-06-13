package com.community.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.constant.MessageConstant;
import com.community.constant.StatusConstant;
import com.community.dto.ProductBundleDTO;
import com.community.dto.ProductBundlePageQueryDTO;
import com.community.entity.Product;
import com.community.entity.ProductBundle;
import com.community.entity.ProductBundleItem;
import com.community.exception.DeletionNotAllowedException;
import com.community.exception.ProductBundleEnableFailedException;
import com.community.mapper.ProductMapper;
import com.community.mapper.ProductBundleItemMapper;
import com.community.mapper.ProductBundleMapper;
import com.community.result.PageResult;
import com.community.service.ProductBundleService;
import com.community.vo.ProductItemVO;
import com.community.vo.ProductBundleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class ProductBundleServiceImpl implements ProductBundleService {

    @Autowired
    private ProductBundleMapper productBundleMapper;
    @Autowired
    private ProductBundleItemMapper productBundleItemMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param productBundleDTO
     */
    @Transactional
    public void saveWithProduct(ProductBundleDTO productBundleDTO) {
        ProductBundle setmeal = new ProductBundle();
        BeanUtils.copyProperties(productBundleDTO, setmeal);

        //向套餐表插入数据
        productBundleMapper.insert(setmeal);

        //获取生成的套餐id
        Long productBundleId = setmeal.getId();

        List<ProductBundleItem> productBundleItems = productBundleDTO.getProductBundleItems();
        productBundleItems.forEach(productBundleItem -> {
            productBundleItem.setProductBundleId(productBundleId);
        });

        //保存套餐和菜品的关联关系
        productBundleItemMapper.insertBatch(productBundleItems);
    }

    /**
     * 分页查询
     *
     * @param productBundlePageQueryDTO
     * @return
     */
    public PageResult pageQuery(ProductBundlePageQueryDTO productBundlePageQueryDTO) {
        int pageNum = productBundlePageQueryDTO.getPage();
        int pageSize = productBundlePageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<ProductBundleVO> page = productBundleMapper.pageQuery(productBundlePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            ProductBundle setmeal = productBundleMapper.getById(id);
            if (StatusConstant.ENABLE == setmeal.getStatus()) {
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(productBundleId -> {
            //删除套餐表中的数据
            productBundleMapper.deleteById(productBundleId);
            //删除套餐菜品关系表中的数据
            productBundleItemMapper.deleteByProductBundleId(productBundleId);
        });
    }

    /**
     * 根据id查询套餐和套餐菜品关系
     *
     * @param id
     * @return
     */
    public ProductBundleVO getByIdWithProduct(Long id) {
        ProductBundleVO productBundleVO = productBundleMapper.getByIdWithProduct(id);
        return productBundleVO;
    }

    /**
     * 修改套餐
     *
     * @param productBundleDTO
     */
    @Transactional
    public void update(ProductBundleDTO productBundleDTO) {
        ProductBundle setmeal = new ProductBundle();
        BeanUtils.copyProperties(productBundleDTO, setmeal);

        //1、修改套餐表，执行update
        productBundleMapper.update(setmeal);

        //套餐id
        Long productBundleId = productBundleDTO.getId();

        //2、删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        productBundleItemMapper.deleteByProductBundleId(productBundleId);

        List<ProductBundleItem> productBundleItems = productBundleDTO.getProductBundleItems();
        productBundleItems.forEach(productBundleItem -> {
            productBundleItem.setProductBundleId(productBundleId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        productBundleItemMapper.insertBatch(productBundleItems);
    }

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if (status == StatusConstant.ENABLE) {
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Product> productList = productMapper.getByProductBundleId(id);
            if (productList != null && productList.size() > 0) {
                productList.forEach(dish -> {
                    if (StatusConstant.DISABLE == dish.getStatus()) {
                        throw new ProductBundleEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        ProductBundle setmeal = ProductBundle.builder()
                .id(id)
                .status(status)
                .build();
        productBundleMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<ProductBundle> list(ProductBundle setmeal) {
        List<ProductBundle> list = productBundleMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<ProductItemVO> getProductItemById(Long id) {
        return productBundleMapper.getProductItemByProductBundleId(id);
    }
}
