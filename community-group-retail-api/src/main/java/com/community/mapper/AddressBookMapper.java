package com.community.mapper;

import com.community.entity.AddressBook;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 条件查询
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     * @param addressBook
     */
    @Insert("insert into addr_book" +
            "        (uid, consignee, phone, sex, prov_code, prov_name, city_code, city_name, dist_code," +
            "         dist_name, detail, label, is_def)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select("select id, uid as user_id, consignee, sex, phone, prov_code as province_code, prov_name as province_name, " +
            "city_code, city_name, dist_code as district_code, dist_name as district_name, detail, label, is_def as is_default " +
            "from addr_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据id修改
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据 用户id修改 是否默认地址
     * @param addressBook
     */
    @Update("update addr_book set is_def = #{isDefault} where uid = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from addr_book where id = #{id}")
    void deleteById(Long id);

}
