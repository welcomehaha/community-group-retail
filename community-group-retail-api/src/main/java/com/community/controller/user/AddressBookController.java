package com.community.controller.user;

import com.community.context.BaseContext;
import com.community.dto.AddressBookDefaultDTO;
import com.community.dto.AddressBookSaveDTO;
import com.community.dto.AddressBookUpdateDTO;
import com.community.entity.AddressBook;
import com.community.result.Result;
import com.community.service.AddressBookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Validated
@Tag(name = "C端地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    @Operation(summary = "新增地址")
    public Result save(@Valid @RequestBody AddressBookSaveDTO addressBookDTO) {
        // 使用 DTO 做参数校验，再复制到实体对象，避免前端传入无关字段污染持久化实体。
        AddressBook addressBook = new AddressBook();
        BeanUtils.copyProperties(addressBookDTO, addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    @Operation(summary = "根据id修改地址")
    public Result update(@Valid @RequestBody AddressBookUpdateDTO addressBookDTO) {
        // 编辑地址时强制要求地址 ID 存在，并沿用统一字段校验规则。
        AddressBook addressBook = new AddressBook();
        BeanUtils.copyProperties(addressBookDTO, addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result setDefault(@Valid @RequestBody AddressBookDefaultDTO addressBookDTO) {
        // 默认地址只需要地址 ID，避免请求体承载过多无关字段。
        AddressBook addressBook = new AddressBook();
        BeanUtils.copyProperties(addressBookDTO, addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @Operation(summary = "根据id删除地址")
    public Result deleteById(@NotNull(message = "地址ID不能为空") Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @Operation(summary = "查询默认地址")
    public Result<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("没有查询到默认地址");
    }

}


