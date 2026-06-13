package com.community.mapper;

import com.github.pagehelper.Page;
import com.community.annotation.AutoFill;
import com.community.dto.ProductPageQueryDTO;
import com.community.entity.Product;
import com.community.enumeration.OperationType;
import com.community.vo.ProductVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from prod where cat_id = #{categoryId}")
    Integer countByProductCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Product dish);

    /**
     * 菜品分页查询
     *
     * @param productPageQueryDTO
     * @return
     */
    Page<ProductVO> pageQuery(ProductPageQueryDTO productPageQueryDTO);

    /**
     * 根据主键查询菜品
     *
     * @param id
     * @return
     */
    @Select("select id, name, cat_id as productCategory_id, price, img as image, descr as description, status, " +
            "create_time, update_time, create_uid as create_user, update_uid as update_user from prod where id = #{id}")
    Product getById(Long id);

    /**
     * 根据主键删除菜品数据
     *
     * @param id
     */
    @Delete("delete from prod where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id动态修改菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Product dish);

    /**
     * 动态条件查询菜品
     *
     * @param dish
     * @return
     */
    List<Product> list(Product dish);

    /**
     * 根据套餐id查询菜品
     * @param productBundleId
     * @return
     */
    @Select("select a.id, a.name, a.cat_id as productCategory_id, a.price, a.img as image, a.descr as description, a.status, " +
            "a.create_time, a.update_time, a.create_uid as create_user, a.update_uid as update_user " +
            "from prod a left join pack_item b on a.id = b.prod_id where b.pack_id = #{productBundleId}")
    List<Product> getByProductBundleId(Long productBundleId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
