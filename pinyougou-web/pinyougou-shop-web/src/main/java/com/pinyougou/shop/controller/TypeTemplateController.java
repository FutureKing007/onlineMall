package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.cart.service.TypeTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    @GetMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        try {
            return typeTemplateService.findOne(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/findSpecByTemplateId")
    public List<Map> findSpecByTemplateId(Long id) {
        try {
            return typeTemplateService.findSpecByTemplateId(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
