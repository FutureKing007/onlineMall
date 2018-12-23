package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class PageMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void onMessage(TextMessage textMessage, Session session) throws JMSException {
        try {
            //得到消息
            String goodsId = textMessage.getText();
            System.out.println("goodsId: " + goodsId);
            //根据模板文件获得模板对象
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
            //得到数据模型     
            Map<String, Object> dataModel = goodsService.getGoods(Long.valueOf(goodsId));
            //创建转换流对象
            OutputStreamWriter outputStreamWriter
                    = new OutputStreamWriter(
                            new FileOutputStream(pageDir + goodsId + ".html"),"UTF-8");

            //创建文件
            template.process(dataModel,outputStreamWriter);
            //关闭输出流
            outputStreamWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
