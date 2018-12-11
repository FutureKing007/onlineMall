package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceimpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;


    @Override
    public void save(Goods goods) {
        try {
            goodsMapper.insertSelective(goods);
            GoodsDesc goodsDesc = goods.getGoodsDesc();
            goodsDesc.setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goodsDesc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
