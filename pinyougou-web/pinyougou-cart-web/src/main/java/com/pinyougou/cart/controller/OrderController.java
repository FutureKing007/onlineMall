package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 3000)
    private OrderService orderService;

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    @PostMapping("/save")
    public boolean save(@RequestBody Order order
            , HttpServletRequest request) {
        try {
            //获取登录用户名
            String userId = request.getRemoteUser();
            //设置用户名
            order.setUserId(userId);
            //设置订单来源
            order.setSourceType("2");

            orderService.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
//        //创建分布式idworker id生成器
//        IdWorker idWorker = new IdWorker();

//        return weixinPayService.genPayCode(String.valueOf(idWorker.nextId()), "1");

        //首先获取用户名
        String userId = request.getRemoteUser();
        //从redis中得到payLog
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        return weixinPayService.genPayCode(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
    }

    @PostMapping("/queryPayStatus")
    public Map<String,Integer> queryPayStatus(String outTradeNo) {
        //创建Map对象作为返回对象
        Map<String, Integer> data = new HashMap<>();
        //先把是status : 3 放进map, 代表没有操作
        data.put("status", 3);
        Map<String, String> resMap = weixinPayService.queryPayStatus(outTradeNo);
        if (resMap!=null && resMap.size() > 0) {
            //判断返回的状态码 返回状态码	return_code	是	String(16)
            if ("SUCCESS".equals(resMap.get("trade_state"))) {
                //修改订单状态
                //传入交易单号,以及交易流水单号(微信返回的)
                orderService.updateOrderStatus(outTradeNo,resMap.get("transaction_id"));
                data.put("status", 1);
            }
            if ("NOTPAY".equals(resMap.get("trade_state"))) {
                data.put("status", 2);
            }
        }
        return data;
    }

}
