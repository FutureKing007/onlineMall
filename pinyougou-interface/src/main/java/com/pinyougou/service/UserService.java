package com.pinyougou.service;

import com.pinyougou.pojo.User;

public interface UserService {
    void save(User user);

    boolean sendCode(String phone);

    boolean checkSmsCode(String phone, String smsCode);
}
