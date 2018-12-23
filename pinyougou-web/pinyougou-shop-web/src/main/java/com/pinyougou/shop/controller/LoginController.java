package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(String username, String password
            , HttpServletRequest request) {

//        response.setContentType("text/plain;charset=utf-8");

        if (request.getMethod().equalsIgnoreCase("post")) {
            //得到提交的验证码
            String vcodeSubmitted = request.getParameter("check");
            //得到session中的验证码
            String vcodeInSession = (String) request.getSession().getAttribute("vcode");

            System.out.println("提交的验证码:  " + vcodeSubmitted); // 最后删除
            System.out.println("在session验证码:  " + vcodeInSession);//最后删除
            System.out.println(username + "==" + password);  // 最后删除

            //判断验证码
            if (!vcodeSubmitted.equalsIgnoreCase(vcodeInSession)) {

//                try {
//                    response.getWriter().write("验证码不正确");
//                    return null;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                throw new RuntimeException("11211");
                return "redirect:/shoplogin.html";
            }

            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(username, password);

            try {
                Authentication authenticate = authenticationManager.authenticate(token);
                if (authenticate.isAuthenticated()) {
                    //设置用户认证成功,往Session中添加认证通过的信息
                    SecurityContextHolder.getContext().setAuthentication(authenticate);
                    //重定向到登录成功页面
                    return "redirect:/admin/index.html";
                } else {
//                    response.getWriter().write("用户名或密码不正确");
                    return "redirect:/shoplogin.html";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/shoplogin.html";
    }

//    @PostMapping("/login")
//    @ResponseBody
//    public String login(String username, String password, String check, HttpServletRequest request) {
//
////        response.setContentType("text/plain;charset=utf-8");
//
//        if (request.getMethod().equalsIgnoreCase("post")) {
//            //得到提交的验证码
//
//            //得到session中的验证码
//            String vcodeInSession = (String) request.getSession().getAttribute("vcode");
//
//            System.out.println("提交的验证码:  " + check); // 最后删除
//            System.out.println("在session验证码:  " + vcodeInSession);//最后删除
//            System.out.println(username + "==" + password);  // 最后删除
//
//            //判断验证码
//            if (!check.equalsIgnoreCase(vcodeInSession)) {
//
////                try {
////                    response.getWriter().write("验证码不正确");
////                    return null;
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//                return "验证码不正确";
//            }
//
//            UsernamePasswordAuthenticationToken token
//                    = new UsernamePasswordAuthenticationToken(username, password);
//
//            try {
//                Authentication authenticate = authenticationManager.authenticate(token);
//                if (authenticate.isAuthenticated()) {
//                    //设置用户认证成功,往Session中添加认证通过的信息
//                    SecurityContextHolder.getContext().setAuthentication(authenticate);
//                    //重定向到登录成功页面
//                    return "redirect:/admin/index.html";
//                } else {
////                    response.getWriter().write("用户名或密码不正确");
//                    return "用户名或密码不正确";
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return "redirect:/shoplogin.html";
//    }

    @GetMapping("/showLoginName")
    @ResponseBody
    public Map<String,String> showLoginName() {
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }
}
