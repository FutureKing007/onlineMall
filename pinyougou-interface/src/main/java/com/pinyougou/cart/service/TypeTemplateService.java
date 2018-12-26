package com.pinyougou.cart.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    PageResult findByPage(TypeTemplate typeTemplate, Integer page, Integer rows);

    void save(TypeTemplate typeTemplate);

    void udpate(TypeTemplate typeTemplate);

    void deleteAll(Serializable[] ids);

    List<Map<String,Object>> findTypeIdAndNameList();

    TypeTemplate findOne(Serializable id);

    List<Map> findSpecByTemplateId(Serializable id);
}
