package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.Item;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GoodsService {
    void save(Goods goods);

    PageResult findByPage(Goods goods, Integer page, Integer rows);

    void updateStatus(String columnName, Serializable[] ids, String status);

    Map<String,Object> getGoods(Serializable goodsId);

    List<Item> findItemByGoodsId(Serializable[] ids);
}
