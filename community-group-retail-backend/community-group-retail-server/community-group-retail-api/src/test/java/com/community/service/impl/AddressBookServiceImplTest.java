package com.community.service.impl;

import com.community.context.BaseContext;
import com.community.entity.AddressBook;
import com.community.exception.AddressBookBusinessException;
import com.community.mapper.AddressBookMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressBookServiceImplTest {

    @Mock
    private AddressBookMapper addressBookMapper;

    @InjectMocks
    private AddressBookServiceImpl addressBookService;

    private AddressBook ownedAddressBook;
    private AddressBook otherUserAddressBook;

    @BeforeEach
    void setUp() {
        BaseContext.setCurrentId(1001L);

        ownedAddressBook = AddressBook.builder()
                .id(10L)
                .userId(1001L)
                .consignee("张三")
                .phone("13812345678")
                .build();

        otherUserAddressBook = AddressBook.builder()
                .id(20L)
                .userId(2002L)
                .consignee("李四")
                .phone("13912345678")
                .build();
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void shouldReturnAddressWhenOwnedByCurrentUser() {
        when(addressBookMapper.getById(10L)).thenReturn(ownedAddressBook);

        AddressBook result = addressBookService.getById(10L);

        assertEquals(10L, result.getId());
        assertEquals(1001L, result.getUserId());
    }

    @Test
    void shouldRejectQueryWhenAddressBelongsToAnotherUser() {
        when(addressBookMapper.getById(20L)).thenReturn(otherUserAddressBook);

        AddressBookBusinessException exception =
                assertThrows(AddressBookBusinessException.class, () -> addressBookService.getById(20L));

        assertEquals("地址不存在或无权限访问", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateWhenAddressBelongsToAnotherUser() {
        when(addressBookMapper.getById(20L)).thenReturn(otherUserAddressBook);

        AddressBook updateRequest = AddressBook.builder()
                .id(20L)
                .consignee("越权修改")
                .build();

        AddressBookBusinessException exception =
                assertThrows(AddressBookBusinessException.class, () -> addressBookService.update(updateRequest));

        assertEquals("地址不存在或无权限访问", exception.getMessage());
        verify(addressBookMapper, never()).update(any(AddressBook.class));
    }

    @Test
    void shouldRejectDeleteWhenAddressBelongsToAnotherUser() {
        when(addressBookMapper.getById(20L)).thenReturn(otherUserAddressBook);

        AddressBookBusinessException exception =
                assertThrows(AddressBookBusinessException.class, () -> addressBookService.deleteById(20L));

        assertEquals("地址不存在或无权限访问", exception.getMessage());
        verify(addressBookMapper, never()).deleteById(20L);
    }

    @Test
    void shouldRejectSetDefaultWhenAddressBelongsToAnotherUser() {
        when(addressBookMapper.getById(20L)).thenReturn(otherUserAddressBook);

        AddressBook defaultRequest = AddressBook.builder().id(20L).build();

        AddressBookBusinessException exception =
                assertThrows(AddressBookBusinessException.class, () -> addressBookService.setDefault(defaultRequest));

        assertEquals("地址不存在或无权限访问", exception.getMessage());
        verify(addressBookMapper, never()).updateIsDefaultByUserId(any(AddressBook.class));
        verify(addressBookMapper, never()).update(any(AddressBook.class));
    }

    @Test
    void shouldUpdateDefaultAddressWhenOwnedByCurrentUser() {
        when(addressBookMapper.getById(10L)).thenReturn(ownedAddressBook);

        AddressBook defaultRequest = AddressBook.builder().id(10L).build();

        assertDoesNotThrow(() -> addressBookService.setDefault(defaultRequest));

        verify(addressBookMapper, times(1)).updateIsDefaultByUserId(any(AddressBook.class));
        verify(addressBookMapper, times(1)).update(any(AddressBook.class));
    }
}
