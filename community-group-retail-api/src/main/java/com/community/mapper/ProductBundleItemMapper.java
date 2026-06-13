package com.community.mapper;

import com.community.entity.ProductBundleItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductBundleItemMapper {
    /**
     * 根据菜品id查询对应的套餐id
     *
     * @param productIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getProductBundleIdsByProductIds(List<Long> productIds);

    /**
     * 批量保存套餐和菜品的关联关系
     *
     * @param productBundleItems
     */
    void insertBatch(List<ProductBundleItem> productBundleItems);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     *
     * @param productBundleId
     */
    @Delete("delete from pack_item where pack_id = #{productBundleId}")
    void deleteByProductBundleId(Long productBundleId);
}
