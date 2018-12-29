package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerControllerService;
import com.pinyougou.utils.ConvertEnodingUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerControllerService sellerControllerService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, Integer rows) {
        if (seller.getName() != null && !seller.getName().isEmpty()) {
            seller.setName(ConvertEnodingUtils.ConvertIosToUTF(seller.getName()));
        }
        if (seller.getNickName() != null && !seller.getNickName().isEmpty()) {
            seller.setNickName(ConvertEnodingUtils.ConvertIosToUTF(seller.getNickName()));
        }

        return sellerControllerService.findByPage(seller, page, rows);
    }

    @GetMapping("/updateStatus")
    public boolean updateStatus(String sellerId,String status) {
        try {
            sellerControllerService.updateStatus(sellerId,status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
