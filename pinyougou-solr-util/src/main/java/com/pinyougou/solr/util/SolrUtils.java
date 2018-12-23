package com.pinyougou.solr.util;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData() {
        //创建一个Item对象
        Item newItem = new Item();
        //设置Item对象的状态
        newItem.setStatus("1");
        //itemMapper查询item对象返回符合条件的List
        List<Item> itemList = itemMapper.select(newItem);
        //创建一个新的SolrItem 的list集合
        List<SolrItem> solrItems = new ArrayList<>();

        System.out.println("====商品列表====");
        for (Item item : itemList) {
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
        }
        System.out.println("========结束=======");
        //solrItems保存到索引库中
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        //提交或回滚事务
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }

    }

    public static void main(String[]args) {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SolrUtils solrUtils = applicationContext.getBean(SolrUtils.class);
        solrUtils.importItemData();
    }


}
