package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.service.WeixinPayService;
import com.pinyougou.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service(interfaceName = "com.pinyougou.service.WeixinPayService")
public class WeixinPayServiceImpl implements WeixinPayService {


    @Value("${appid}")
    private String appid;

    @Value("${mchId}")
    private String mchId;

    @Value("${partnerkey}")
    private String partnerKey;

    @Value("${unifiedorder}")
    private String unifiedorder;

    @Value("${orderquery}")
    private String orderquery;



    @Override
    public Map<String, String> genPayCode(String outTradeNo, String totalFee) {
        //创建Map集合封装请求的参数
        Map<String, String> param = new HashMap<>();
        //公众账号ID	appid	是
        param.put("appid", appid);
        //商户号	mch_id	是
        param.put("mch_id", mchId);
        //随机字符串	nonce_str	是
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述	body	是
        param.put("body", "您好啊,小小乔");
        //商户订单号	out_trade_no	是
        param.put("out_trade_no", outTradeNo);
        //标价金额	total_fee	是	Int	88	订单总金额，单位为分
        param.put("total_fee", totalFee);
        //终端IP	spbill_create_ip	是
        param.put("spbill_create_ip", "127.0.0.1");
        //通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php
        // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "http://www.jingxi.com");

        //交易类型	trade_type	是	String(16)	JSAPI
        //JSAPI -JSAPI支付
        //NATIVE -Native支付
        //APP -APP支付
        //说明详见参数规定
        param.put("trade_type", "NATIVE");
        try {
            //签名	sign	是
            //用WXPayUtil工具类根据签名生成XML类型的请求参数
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerKey);
            System.out.println("请求参数: " + xmlParam);
            //创建HttpClientUtil对象
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            //发送请求
            String result = httpClientUtils.sendPost(unifiedorder, xmlParam);
            System.out.println("响应数据: " + result);
            //把响应的数据转换为Map类型
            Map<String, String> resMap = WXPayUtil.xmlToMap(result);
            //创建新的Map对象封装 outTradeNo,totalFee,code_url
            Map<String, String> data = new HashMap<>();
            //封装outTradeNo
            data.put("outTradeNo", outTradeNo);
            //封装totalFee
            data.put("totalFee", totalFee);
            //封装code_url
            data.put("codeUrl", resMap.get("code_url"));
            return data;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            //创建新的Map对象封装提交的数据
            Map<String,String> params = new HashMap<>();
            //公众账号ID	appid	是	String(32)
            params.put("appid",appid);
            //商户号	mch_id	是	String(32)	1230000109
            params.put("mch_id",mchId);
            //商户订单号	out_trade_no	String(32)	20150806125346
            params.put("out_trade_no",outTradeNo);
            //随机字符串	nonce_str	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6
            params.put("nonce_str",WXPayUtil.generateNonceStr());
            //签名	sign	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS
            //Map对象装换为XML文件对象
            String parmsXml = WXPayUtil.generateSignedXml(params, partnerKey);
            //输出请求参数
            System.out.println("请求参数" + parmsXml);
            //使用HttpClientsUtils对象发送请求
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            //发送post请求
            String responseXml = httpClientUtils.sendPost(orderquery, parmsXml);
            //输出响应的数据
            System.out.println("响应数据:  "+responseXml);
            //此时响应对象是xml文件,把xml文件转换为Map对象
            return WXPayUtil.xmlToMap(responseXml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
