package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandController {

//    @Autowired(required = false)
    @Reference(timeout = 10000)
    private BrandService brandService;


    /*查询全部品牌*/
    @GetMapping("/brand/findAll") //  == @ReqeustMapping(method = "Get")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

}
