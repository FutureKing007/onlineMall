package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface  BrandService {
    List<Brand> findAll();

    void save(Brand brand);

    void Update(Brand brand);


    PageResult findByPage(Brand brand, Integer page, Integer rows);

    void delete(Serializable[] ids);


    List<Map<String,Object>> findAllByIdAndName();
}
