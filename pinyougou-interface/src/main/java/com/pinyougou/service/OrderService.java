package com.pinyougou.service;

import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;

public interface OrderService {
    void save(Order order);

    PayLog findPayLogFromRedis(String userId);

    void updateOrderStatus(String outTradeNo, String transactionId);
}
