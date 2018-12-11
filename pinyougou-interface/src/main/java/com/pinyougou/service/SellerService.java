package com.pinyougou.service;

import com.pinyougou.pojo.Seller;

import java.io.Serializable;

public interface SellerService {
    Seller findOne(Serializable id);
}
