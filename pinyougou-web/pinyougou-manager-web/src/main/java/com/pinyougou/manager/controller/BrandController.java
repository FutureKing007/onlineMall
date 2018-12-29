package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand, Integer page, Integer rows) {
//        Get请求中文转码
        if (brand != null & StringUtils.isNoneBlank(brand.getName())) {
            try {
                brand.setName(new String(brand.getName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        PageResult pageResult = brandService.findByPage(brand, page, rows);
        return pageResult;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @GetMapping("findBrandList")
    public List<Map<String,Object>> findBrandList() {
        try {
            return brandService.findAllByIdAndName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
