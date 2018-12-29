package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 3000)
    private GoodsService goodsService;

    @Reference(timeout = 3000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("=====ItemMessageListener=====");
        Long[] ids = (Long[]) objectMessage.getObject();

        //根据goodsId查到Item的集合
        List<Item> itemList = goodsService.findItemByGoodsId(ids);

        //创建一个新的SolrItem 的list集合
        List<SolrItem> solrItems = new ArrayList<>();

        //遍历itemList
        itemList.forEach(item -> {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item.getId());
            solrItem.setTitle(item.getTitle());
            solrItem.setPrice(new BigDecimal(item.getPrice().doubleValue()));
            solrItem.setImage(item.getImage());
            solrItem.setGoodsId(item.getGoodsId());
            solrItem.setCategory(item.getCategory());
            solrItem.setBrand(item.getBrand());
            solrItem.setSeller(item.getSeller());
            solrItem.setUpdateTime(item.getUpdateTime());
            //封装spec
            //先用Json解析
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            //设置动态域
            solrItem.setSpecMap(specMap);
            //加入solrItemList集合
            solrItems.add(solrItem);
        });
        itemSearchService.saveOrUpdate(solrItems);
    }
}
