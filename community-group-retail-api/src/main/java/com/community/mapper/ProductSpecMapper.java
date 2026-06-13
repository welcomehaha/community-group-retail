package com.community.mapper;

import com.community.entity.ProductSpec;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSpecMapper {
    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<ProductSpec> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param productId
     */
    @Delete("delete from prod_spec where prod_id = #{productId}")
    void deleteByProductId(Long productId);

    /**
     * 根据菜品id查询对应的口味数据
     * @param productId
     * @return
     */
    @Select("select id, prod_id as product_id, name, value from prod_spec where prod_id = #{productId}")
    List<ProductSpec> getByProductId(Long productId);
}
