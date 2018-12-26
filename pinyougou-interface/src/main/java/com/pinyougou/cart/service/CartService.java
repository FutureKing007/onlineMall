package com.pinyougou.cart.service;

import com.pinyougou.pojo.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    List<Cart> findCartRedis(String username);

    void saveCartRedis(String username, List<Cart> carts);

    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> carts);
}
