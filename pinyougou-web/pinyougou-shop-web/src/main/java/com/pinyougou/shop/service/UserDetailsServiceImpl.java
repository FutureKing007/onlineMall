package com.pinyougou.shop.service;

import com.pinyougou.pojo.Seller;
import com.pinyougou.cart.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        创建List集合封装角色
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        添加角色
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
//        返回用户信息对象
//        return new User(username, "123456", grantedAuthorities);
        Seller seller = sellerService.findOne(username);
        if (seller != null && seller.getStatus().equals("1")) {
//          返回用户对象
            return new User(username, seller.getPassword(), grantedAuthorities);
        }
        return null;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
