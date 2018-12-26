package com.pinyougou.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Reference(timeout = 10000)
    private SmsService smsService;

    @PostMapping("/sendSms")
    public Map<String,Object> sendSms(String phoneNum, String signName
            , String templateCode, String templateParam) {
        //发送信息得到返回值
        boolean success = smsService.sendSms(phoneNum, signName
                , templateCode, templateParam);
        //
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        return map;
    }
}
