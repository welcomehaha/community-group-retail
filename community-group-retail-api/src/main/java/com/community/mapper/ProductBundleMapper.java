package com.community.mapper;

import com.github.pagehelper.Page;
import com.community.annotation.AutoFill;
import com.community.dto.ProductBundlePageQueryDTO;
import com.community.entity.ProductBundle;
import com.community.enumeration.OperationType;
import com.community.vo.ProductItemVO;
import com.community.vo.ProductBundleVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductBundleMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from pack where cat_id = #{categoryId}")
    Integer countByProductCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据id修改套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(ProductBundle setmeal);

    /**
     * 新增套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(ProductBundle setmeal);

    /**
     * 分页查询
     * @param productBundlePageQueryDTO
     * @return
     */
    Page<ProductBundleVO> pageQuery(ProductBundlePageQueryDTO productBundlePageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select id, cat_id as productCategory_id, name, price, status, descr as description, img as image, create_time, " +
            "update_time, create_uid as create_user, update_uid as update_user from pack where id = #{id}")
    ProductBundle getById(Long id);

    /**
     * 根据id删除套餐
     * @param productBundleId
     */
    @Delete("delete from pack where id = #{productBundleId}")
    void deleteById(@Param("productBundleId") Long productBundleId);

    /**
     * 根据id查询套餐和套餐菜品关系
     * @param id
     * @return
     */
    ProductBundleVO getByIdWithProduct(Long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<ProductBundle> list(ProductBundle setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param productBundleId
     * @return
     */
    @Select("select sd.name, sd.copies, d.img as image, d.descr as description " +
            "from pack_item sd left join prod d on sd.prod_id = d.id " +
            "where sd.pack_id = #{productBundleId}")
    List<ProductItemVO> getProductItemByProductBundleId(Long productBundleId);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
