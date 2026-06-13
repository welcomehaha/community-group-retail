package com.community.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.constant.MessageConstant;
import com.community.constant.StatusConstant;
import com.community.dto.ProductDTO;
import com.community.dto.ProductPageQueryDTO;
import com.community.entity.Product;
import com.community.entity.ProductSpec;
import com.community.entity.ProductBundle;
import com.community.exception.DeletionNotAllowedException;
import com.community.mapper.ProductSpecMapper;
import com.community.mapper.ProductMapper;
import com.community.mapper.ProductBundleItemMapper;
import com.community.mapper.ProductBundleMapper;
import com.community.result.PageResult;
import com.community.service.ProductService;
import com.community.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSpecMapper productSpecMapper;
    @Autowired
    private ProductBundleItemMapper productBundleItemMapper;
    @Autowired
    private ProductBundleMapper productBundleMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param productDTO
     */
    @Transactional
    public void saveWithFlavor(ProductDTO productDTO) {

        Product dish = new Product();

        BeanUtils.copyProperties(productDTO, dish);

        //向菜品表插入1条数据
        productMapper.insert(dish);

        //获取insert语句生成的主键值
        Long productId = dish.getId();

        List<ProductSpec> flavors = productDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(productSpec -> {
                productSpec.setProductId(productId);
            });
            //向口味表插入n条数据
            productSpecMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param productPageQueryDTO
     * @return
     */
    public PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO) {
        PageHelper.startPage(productPageQueryDTO.getPage(), productPageQueryDTO.getPageSize());
        Page<ProductVO> page = productMapper.pageQuery(productPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品？？
        for (Long id : ids) {
            Product dish = productMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除---是否被套餐关联了？？
        List<Long> productBundleIds = productBundleItemMapper.getProductBundleIdsByProductIds(ids);
        if (productBundleIds != null && productBundleIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据
        for (Long id : ids) {
            productMapper.deleteById(id);
            //删除菜品关联的口味数据
            productSpecMapper.deleteByProductId(id);
        }
    }

    /**
     * 根据id查询菜品和对应的口味数据
     *
     * @param id
     * @return
     */
    public ProductVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Product dish = productMapper.getById(id);

        //根据菜品id查询口味数据
        List<ProductSpec> productSpecs = productSpecMapper.getByProductId(id);

        //将查询到的数据封装到VO
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(dish, productVO);
        productVO.setFlavors(productSpecs);

        return productVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     *
     * @param productDTO
     */
    public void updateWithFlavor(ProductDTO productDTO) {
        Product dish = new Product();
        BeanUtils.copyProperties(productDTO, dish);

        //修改菜品表基本信息
        productMapper.update(dish);

        //删除原有的口味数据
        productSpecMapper.deleteByProductId(productDTO.getId());

        //重新插入口味数据
        List<ProductSpec> flavors = productDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(productSpec -> {
                productSpec.setProductId(productDTO.getId());
            });
            //向口味表插入n条数据
            productSpecMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Product dish = Product.builder()
                .id(id)
                .status(status)
                .build();
        productMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> productIds = new ArrayList<>();
            productIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> productBundleIds = productBundleItemMapper.getProductBundleIdsByProductIds(productIds);
            if (productBundleIds != null && productBundleIds.size() > 0) {
                for (Long productBundleId : productBundleIds) {
                    ProductBundle setmeal = ProductBundle.builder()
                            .id(productBundleId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    productBundleMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    public List<Product> list(Long categoryId) {
        Product dish = Product.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return productMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<ProductVO> listWithFlavor(Product dish) {
        List<Product> productList = productMapper.list(dish);

        List<ProductVO> productVOList = new ArrayList<>();

        for (Product d : productList) {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(d,productVO);

            //根据菜品id查询对应的口味
            List<ProductSpec> flavors = productSpecMapper.getByProductId(d.getId());

            productVO.setFlavors(flavors);
            productVOList.add(productVO);
        }

        return productVOList;
    }
}
