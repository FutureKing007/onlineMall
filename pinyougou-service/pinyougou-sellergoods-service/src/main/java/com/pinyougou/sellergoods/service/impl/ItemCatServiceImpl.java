package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.cart.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceName ="ItemCatService" )
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

    @Override
    public void update(ItemCat itemCat) {
        try {
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Serializable[] ids) {
        Example example = new Example(ItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        itemCatMapper.deleteByExample(example);
        for(Serializable id:ids){
            deleteByParentIdRecursion(id);
        }
    }

    public  void deleteByParentIdRecursion(Serializable id) {
        ItemCat itemCat = new ItemCat();
        itemCat.setParentId((Long) id);
        List<ItemCat> itemCatList = itemCatMapper.select(itemCat);
        if (itemCatList!=null && itemCatList.size() > 0) {
            for(ItemCat itemCatDeleted:itemCatList){
                itemCatMapper.delete(itemCatDeleted);
                deleteByParentIdRecursion(itemCatDeleted.getId());
            }
        }
    }
}
