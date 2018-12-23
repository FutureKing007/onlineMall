package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

public class PageDeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Value("${pageDir}")
    private String pageDir;


    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();

            for(Long goodsId:goodsIds){
                File file = new File(pageDir + goodsId + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
