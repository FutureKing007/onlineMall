package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Cart;
import com.pinyougou.service.CartService;
import com.pinyougou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 3000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @GetMapping("/addCart")
    @CrossOrigin(origins = {"http://item.pinyougou.com","http://search.pinyougou.com"}, allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num) {


        String username = request.getRemoteUser();
//        //设置允许跨域访问的域名
////        response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");
////
////        //设置允许操作Cookie
////        response.setHeader("Access-Control-Allow-Credentials", "true");

        try {
            //首先,先获取购物车的集合
            List<Cart>carts =findCart();

            carts = cartService.addItemToCart(carts, itemId, num);

            if (StringUtils.isNoneBlank(username)) {
                cartService.saveCartRedis(username, carts);
            } else {

                CookieUtils.setCookie(request,response,CookieUtils.CookieName.PINYOUGOU_CART
                        , JSON.toJSONString(carts),86400,true);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/findCart")
    private List<Cart> findCart() {
        //首先利用CookieUtil去得到cart集合的值
        String username = request.getRemoteUser();
        List<Cart> carts = null;
        if (StringUtils.isNoneBlank(username)) {
            //从redis中取购物车
           carts = cartService.findCartRedis(username);
           //从cookie中取购物车
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isNoneBlank(cartStr)) {
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                //合并购物车
                carts=cartService.mergeCart(cookieCarts,carts);
                //将合并后的购物车放到redis中
                cartService.saveCartRedis(username, carts);
                //删除Cookie中的购物车
                CookieUtils.deleteCookie(request,response,CookieUtils.CookieName.PINYOUGOU_CART);

            }

        } else {
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (cartStr == null) {
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;

    }


}
