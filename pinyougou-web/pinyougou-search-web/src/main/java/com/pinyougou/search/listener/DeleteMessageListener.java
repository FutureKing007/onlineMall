package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.ItemSearchService;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 3000)
    private ItemSearchService itemSearchService;

    @Override

    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("=======deleteMessageListener=======");
        Long[] ids = (Long[]) objectMessage.getObject();
        System.out.println("ids: " + Arrays.asList(ids));
        itemSearchService.delete(Arrays.asList(ids));
    }
}
