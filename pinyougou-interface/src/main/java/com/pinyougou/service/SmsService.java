package com.pinyougou.service;

public interface SmsService {
    public boolean sendSms(String phoneNum,String signName
            ,String templateCode, String templateParam);
}
