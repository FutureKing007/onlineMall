package com.pinyougou.cart.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    @Override

    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        //现根据itemId查询到该Item
        Item item = itemMapper.selectByPrimaryKey(itemId);
        //用item拿到Item的sellerId
        String sellerId = item.getSellerId();


        //根据sellerId从carts 里面得到对应的Cart,也就是该商家的购物车
        Cart cart = searchCartBySellerId(carts, sellerId);

        if (cart != null) {
            //判断cart是否null,不是null代表已经有符合该sellerId
            //已经得到该sellerId对应的cart,所以接下来应该得到该商家的List<OrderItem>集合
            List<OrderItem> orderItemList = cart.getOrderItems();
            //然后判断orderItemList 里面有没有存在该商品,如果有,就把数目加上
            OrderItem orderItem = searchOrderItemByItemId(orderItemList, itemId);
            if (orderItem != null) {
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //如果订单明细的购买数小于等于0,则删除
                if (orderItem.getNum() <= 0) {
                    //删除购物车的订单明细(商品)
                    orderItemList.remove(orderItem);
                }
                //如cart的orderItemList的长度等于0,就把cart删除
                if (orderItemList.size() <= 0) {
                    carts.remove(cart);
                }
            } else {
                // ,如果没有就创建一个新的OrderItem对象,添加到orderItemList里面
                //初始化一个新的OrderItem对象
                orderItem = createOrderItem(item, num);
                //把orderItem
                orderItemList.add(orderItem);
            }
            return carts;
        } else {
            //是null代表carts里面还没有该商家的cart
            //初始化一个新的Cart对象
            cart = new Cart();
            //定义sellerId变量
            cart.setSellerId(sellerId);
            //定义sellerName变量
            cart.setSellerName(item.getSeller());
            //定义OrderItems的集合变量
            //先初始化一个List<OrderItem>
            List<OrderItem> orderItemList = new ArrayList<>();
            //初始化一个个OrderItem对象
            OrderItem orderItem = createOrderItem(item, num);
            //把OrderItem加入orderItemList中
            orderItemList.add(orderItem);
            cart.setOrderItems(orderItemList);
            carts.add(cart);
            return carts;
        }

    }

    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        for (OrderItem orderItem : orderItemList) {
            if (itemId.equals(orderItem.getItemId())) {
                return orderItem;
            }
        }
        return null;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        //先遍历carts,看是否符合该sellerId的Cart对象
        for (Cart cart : carts) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }

    @Override
    public List<Cart> findCartRedis(String username) {
        System.out.println("获取Redis中的购物车: " + username);
        List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
        if (carts == null) {
            carts = new ArrayList<>();
        }
        return carts;
    }

    @Override
    public void saveCartRedis(String username, List<Cart> carts) {
        System.out.println("往redis中存购物车:" + username);
        redisTemplate.boundValueOps("cart_" + username).set(carts);
    }

    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts) {
        for (Cart cart : cookieCarts) {
            for (OrderItem orderItem:cart.getOrderItems()) {
                addItemToCart(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }
}
