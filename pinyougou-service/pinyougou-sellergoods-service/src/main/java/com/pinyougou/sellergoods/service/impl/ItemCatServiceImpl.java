package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceName ="com.pinyougou.service.ItemCatService" )
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    ItemCatMapper itemCatMapper;

    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        ItemCat itemCat = new ItemCat();
        itemCat.setParentId(parentId);
        return itemCatMapper.select(itemCat);
    }

    @Override
    public void save(ItemCat itemCat) {
        try {
            itemCatMapper.insertSelective(itemCat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
