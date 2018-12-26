package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.cart.service.GoodsService;
import com.pinyougou.utils.ConvertEnodingUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination solrQueue;

    @Autowired
    private Destination solrDeleteQueue;

    @Autowired
    private Destination pageTopic;

    @Autowired
    private Destination pageDeleteTopic;

    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods) {
        try {
            goods.setAuditStatus("0");
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods,Integer page,Integer rows) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            if (StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(ConvertEnodingUtils.ConvertIosToUTF(goods.getGoodsName()));
            }

            return goodsService.findByPage(goods, page, rows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/updateStatus")
    public boolean updateStatus(String columnName, Long[] ids, String status) {
        try {
            //更新状态
            goodsService.updateStatus(columnName,ids,status);

            if ("is_marketable".equalsIgnoreCase(columnName)) {
                //1代表上架
                if ("1".equals(status)) {
                    //
                    jmsTemplate.send(solrQueue, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createObjectMessage(ids);
                        }
                    });
                    //
                    for(Long id:ids){
                        jmsTemplate.send(pageTopic,new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                TextMessage textMessage = session.createTextMessage();
                                textMessage.setText(id.toString());
                                return textMessage;
                            }
                        });
                    }
                    //
                } else {
                    //下架
                    jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createObjectMessage(ids);
                        }
                    });
                    for(Long id:ids){
                        jmsTemplate.send(pageDeleteTopic,new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                return session.createObjectMessage(ids);
                            }
                        });
                    }
                }

            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
