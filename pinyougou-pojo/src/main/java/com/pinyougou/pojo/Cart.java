package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车实体类
 */
public class Cart implements Serializable{
    //商家ID
    private String sellerId;
    //商家名称
    private String sellerName;
    //购物车列表
    private List<OrderItem> orderItems;

    public Cart() {
    }

    public Cart(String sellerId, String sellerName, List<OrderItem> orderItems) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.orderItems = orderItems;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
