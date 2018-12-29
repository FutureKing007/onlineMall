package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import com.pinyougou.utils.ConvertEnodingUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods) {
        try {
            goods.setAuditStatus("0");
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        try {
//            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
//            goods.setSellerId(sellerId);
//            添加查询条件
            goods.setAuditStatus("0");
            if (StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(ConvertEnodingUtils.ConvertIosToUTF(goods.getGoodsName()));
            }

            return goodsService.findByPage(goods, page, rows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/updateStatus")
    public boolean updateStatus(String columnName, Long[] ids, String status) {
        try {
            goodsService.updateStatus(columnName,ids,status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
