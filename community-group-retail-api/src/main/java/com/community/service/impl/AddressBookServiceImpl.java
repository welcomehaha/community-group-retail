package com.community.service.impl;

import com.community.exception.AddressBookBusinessException;
import com.community.context.BaseContext;
import com.community.entity.AddressBook;
import com.community.mapper.AddressBookMapper;
import com.community.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 地址不存在或无权访问提示语。
     */
    private static final String ADDRESS_BOOK_NOT_FOUND_OR_FORBIDDEN = "地址不存在或无权限访问";

    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     */
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        return requireOwnedAddress(id);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        // 修改前先校验地址归属，避免越权修改其他用户地址。
        requireOwnedAddress(addressBook.getId());
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Transactional
    public void setDefault(AddressBook addressBook) {
        // 设置默认地址前，先校验目标地址属于当前用户。
        requireOwnedAddress(addressBook.getId());

        //1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void deleteById(Long id) {
        // 删除前先校验地址归属，避免越权删除其他用户地址。
        requireOwnedAddress(id);
        addressBookMapper.deleteById(id);
    }

    /**
     * 校验地址是否存在且归属于当前登录用户。
     *
     * @param id 地址ID
     * @return 当前用户拥有的地址
     */
    private AddressBook requireOwnedAddress(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        Long currentUserId = BaseContext.getCurrentId();
        if (addressBook == null || addressBook.getUserId() == null || !addressBook.getUserId().equals(currentUserId)) {
            throw new AddressBookBusinessException(ADDRESS_BOOK_NOT_FOUND_OR_FORBIDDEN);
        }
        return addressBook;
    }

}
