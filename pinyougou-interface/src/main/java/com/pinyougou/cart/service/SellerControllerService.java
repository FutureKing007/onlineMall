package com.pinyougou.cart.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;

public interface SellerControllerService {
    void save(Seller seller);

    PageResult findByPage(Seller seller, Integer page, Integer rows);

    void updateStatus(String sellerId, String status);
}
