package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AddressController {

    @Reference
    private AddressService addressService;

    @GetMapping("/order/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request) {
        //得到登录的userId
        String userId = request.getRemoteUser();
        //
        return addressService.findAddressByUser(userId);
    }


}
