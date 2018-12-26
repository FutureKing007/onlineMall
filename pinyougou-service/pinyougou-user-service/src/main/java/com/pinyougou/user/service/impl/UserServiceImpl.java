package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.cart.service.UserService;
import com.pinyougou.utils.HttpClientUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service(interfaceName ="UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;



    @Override
    public void save(User user) {
        try {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean sendCode(String phone) {
        try {
            //生成6位随机的验证码
            String code = UUID.randomUUID().toString().replaceAll("-", "")
                    .replaceAll("[a-z|A-Z]", "").substring(0, 6);
            //创建httpclientUtils
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            //创建Map作为传入的参数
            Map<String, String> param = new HashMap<>();
            //设置属性
            param.put("phoneNum", phone);
            param.put("signName", signName);
            param.put("templateCode", templateCode);
            param.put("templateParam", "{\"number\":\"" + code + "\"}");
            //发送post请求
            String content = httpClientUtils.sendPost(smsUrl, param);
            //把传回来的json字符串转换为Map对象
            Map<String,Object>resMap = JSON.parseObject(content, Map.class);
            //传入redis 中
            redisTemplate.boundValueOps(phone).set(code,90, TimeUnit.SECONDS);
            return (boolean) resMap.get("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        String sysCode = (String) redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(sysCode) && sysCode.equalsIgnoreCase(smsCode);
    }

}
