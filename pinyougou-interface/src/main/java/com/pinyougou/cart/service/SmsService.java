package com.pinyougou.cart.service;

public interface SmsService {
    public boolean sendSms(String phoneNum,String signName
            ,String templateCode, String templateParam);
}
