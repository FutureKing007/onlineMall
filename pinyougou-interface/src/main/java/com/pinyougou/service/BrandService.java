package com.pinyougou.service;

import com.pinyougou.pojo.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> findAll();

    void save(Brand brand);

    void Update(Brand brand);

}
