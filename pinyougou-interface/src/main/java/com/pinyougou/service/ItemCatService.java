package com.pinyougou.service;

import com.pinyougou.pojo.ItemCat;

import java.io.Serializable;
import java.util.List;

public interface ItemCatService {
    List<ItemCat> findItemCatByParentId(Long parentId);

    void save(ItemCat itemCat);

    void delete(Serializable[] ids);

    void update(ItemCat itemCat);
}
