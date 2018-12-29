package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(interfaceName ="com.pinyougou.service.OrderService" )
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public void save(Order order) {
        //先根据用户名获得用户的购物车里面的东西
        List<Cart> carts =
                (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();
        //创建List集合,用来封装订单号(orderId)
        List<String> orderIdList = new ArrayList<>();
        //创建变量记录总金额
        double totalMoney = 0;

        //遍历购物车的数据
        for (Cart cart : carts) {
            /*============往订单里插入数据===========*/
            long orderId = idWorker.nextId();
            //得到购物车中的OrderItem列表
            List<OrderItem> orderItemList = cart.getOrderItems();
            // 创建新的订单
            Order order1 = new Order();
            // 设置订单id
            order1.setOrderId(orderId);
            // 设置支付类型
            order1.setPaymentType(order.getPaymentType());
            // 设置支付状态码为“未支付”
            order1.setStatus("1");
            // 设置订单创建时间
            order1.setCreateTime(new Date());
            // 设置订单修改时间
            order1.setUpdateTime(order1.getCreateTime());
            // 设置用户名
            order1.setUserId(order.getUserId());
            // 设置收件人地址
            order1.setReceiverAreaName(order.getReceiverAreaName());
            // 设置收件人手机号码
            order1.setReceiverMobile(order.getReceiverMobile());
            // 设置收件人
            order1.setReceiver(order.getReceiver());
            // 设置订单来源
            order1.setSourceType(order.getSourceType());
            // 设置商家id
            order1.setSellerId(cart.getSellerId());

            // 定义该订单总金额
            double money = 0;

            for (OrderItem orderItem : orderItemList) {
                //设置主键id
                orderItem.setId(idWorker.nextId());
                //设置关联的订单id
                orderItem.setOrderId(orderId);
                //累计总金额
                money += orderItem.getTotalFee().doubleValue();
                //保存数到订单明细表
                orderItemMapper.insertSelective(orderItem);
            }
            //设置总付金额
            order1.setPayment(new BigDecimal(money));
            //保存订单
            orderMapper.insertSelective(order1);
            //把orderId封装到orderIdList
            orderIdList.add(String.valueOf(orderId));
            //累计存入payLog的金额
            totalMoney += money;
        }

        //判断是否为微信支付
        if ("1".equals(order.getPaymentType())) {
            PayLog payLog = new PayLog();
            //生成订单交易号
            String outTradeNo = String.valueOf(idWorker.nextId());
            payLog.setOutTradeNo(outTradeNo);
            payLog.setCreateTime(new Date());
            //支付总金额(分)
            payLog.setTotalFee((long) (totalMoney * 100));
            //用户ID
            payLog.setUserId(order.getUserId());
            //支付状态
            payLog.setTradeState("0");
            //先把orderId转换为String类型
            String orderList = orderIdList.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", "");
            payLog.setOrderList(orderList);
            payLog.setPayType("1");

            //存入数据库中的tb_paylog 表
            payLogMapper.insert(payLog);
            //存入缓存里面
            redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
        }
//        删除redis中的购物车
        redisTemplate.delete("cart_" + order.getUserId());
    }

    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        /** 修改支付日志状态 */
        PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
        payLog.setPayTime(new Date());
        //设置交易流水号
        payLog.setTransactionId(transactionId);
        payLog.setTradeState("1"); //1代表已经付款
        //更新操作
        payLogMapper.updateByPrimaryKey(payLog);

        /** 修改订单状态 */
        String orderList = payLog.getOrderList();

        String[] orderIds = orderList.split(",");

        for(String orderId:orderIds){
            //创建一个新的Order对象用作更新使用
            Order order = new Order();
            order.setOrderId(Long.valueOf(orderId));
            order.setPaymentTime(new Date()); // 设置支付时间
            order.setStatus("2"); //2代表支付
            //更新操作
            orderMapper.updateByPrimaryKeySelective(order);
        }
        /** 清除redis缓存的payLog数据,因为支付成功了,就不需要频繁查询操作 */
        redisTemplate.delete("payLog_" + payLog.getUserId());
    }
}
