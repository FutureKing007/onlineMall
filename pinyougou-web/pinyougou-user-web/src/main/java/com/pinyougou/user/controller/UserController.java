package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 3000)
    private UserService userService;

    @PostMapping("/save")
    public boolean save(@RequestBody User user,String smsCode ) {
        try {
            boolean ok = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ok) {
                userService.save(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/sendCode")
    public boolean sendCode(String phone) {
        return userService.sendCode(phone);
    }

    @GetMapping("/showName")
    public Map<String,String> showName() {
        String loginName
                = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }
}
