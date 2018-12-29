package com.pinyougou.service;

import com.pinyougou.pojo.Address;

import java.util.List;

public interface AddressService {
    List<Address> findAddressByUser(String userId);
}
