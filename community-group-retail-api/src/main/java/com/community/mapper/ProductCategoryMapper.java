package com.community.mapper;

import com.github.pagehelper.Page;
import com.community.annotation.AutoFill;
import com.community.enumeration.OperationType;
import com.community.dto.ProductCategoryPageQueryDTO;
import com.community.entity.ProductCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductCategoryMapper {

    /**
     * 插入数据
     * @param productCategory
     */
    @Insert("insert into cat(type, name, sort, status, create_time, update_time, create_uid, update_uid)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(ProductCategory productCategory);

    /**
     * 分页查询
     * @param productCategoryPageQueryDTO
     * @return
     */
    Page<ProductCategory> pageQuery(ProductCategoryPageQueryDTO productCategoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from cat where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id修改分类
     * @param productCategory
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ProductCategory productCategory);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<ProductCategory> list(Integer type);
}
