package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;

import java.io.Serializable;

public interface GoodsService {
    void save(Goods goods);

    PageResult findByPage(Goods goods, Integer page, Integer rows);

    void updateStatus(String columnName, Serializable[] ids, String status);
}
