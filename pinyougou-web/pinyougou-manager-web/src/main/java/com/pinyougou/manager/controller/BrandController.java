package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    //    @Autowired(required = false)
    @Reference(timeout = 10000)
    private BrandService brandService;


    /*查询全部品牌*/
    @GetMapping("/findAll") //  == @ReqeustMapping(method = "Get")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand) {
        try {
            brandService.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand) {
        try {
            brandService.Update(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
