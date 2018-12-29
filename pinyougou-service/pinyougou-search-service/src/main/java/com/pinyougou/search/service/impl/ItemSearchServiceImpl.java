package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
@Transactional
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        //创建Map作为返回对象
        Map<String, Object> data = new HashMap<>();

        //得到keywords里的值
        String keywords = (String) params.get("keywords");

        //得到当前页
        Integer page = getcurrentPage(params);

        //得到页大小
        Integer rows = getRows(params);

        if (StringUtils.isNoneBlank(keywords)) {
            //创建高亮查询对象
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            //创建高亮选项对象
            HighlightOptions highlightOptions = new HighlightOptions();
            //设置高亮域
            highlightOptions.addField("title");
            //设置高亮前缀
            highlightOptions.setSimplePrefix("<font color='red'>");
            //设置高亮后缀
            highlightOptions.setSimplePostfix("</font>");
            //设置高亮选项
            highlightQuery.setHighlightOptions(highlightOptions);

            //根据'title'创建条件对象
            Criteria criteria = new Criteria("title").is(keywords);
            //添加查询条件
            highlightQuery.addCriteria(criteria);

            //添加分类过滤查询条件
            filterCategory(highlightQuery,params);
            //添加品牌过滤查询条件
            filterBrand(highlightQuery,params);
            //添加规格过滤查询条件
            filterSpec(highlightQuery,params);
            //添加价格过滤查询条件
            filterPrice(highlightQuery,params);

            //设置排序
            setSort(highlightQuery, params);

            //设置开始查询数
            highlightQuery.setOffset(getOffset(page, rows));
            //设置每页显示的记录数
            highlightQuery.setRows(rows);

            HighlightPage<SolrItem> solrItems = solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);
           //循环高亮项
            for(HighlightEntry<SolrItem> highlightEntry:solrItems.getHighlighted()){
                //得到检索的到的原实体
                SolrItem solrItem = highlightEntry.getEntity();
                if (highlightEntry.getHighlights().size()>0 &&
                        highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
                    solrItem.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
            data.put("rows", solrItems.getContent());
            //设置总记录数
            data.put("total", solrItems.getTotalElements());
            //设置总页数
            data.put("totalPage", solrItems.getTotalPages());
        } else {
            //创建查询对象
            Query query = new SimpleQuery("*:*");
            //添加分类过滤查询条件
            filterCategory(query,params);
            //添加品牌过滤查询条件
            filterBrand(query,params);
            //添加规格过滤查询条件
            filterSpec(query,params);
            //添加价格过滤查询条件
            filterPrice(query,params);

            //添加排序
            setSort(query,params);

            //设置起始数
            query.setOffset(getOffset(page, rows));
            //设置每页记录
            query.setRows(rows);

            //进行条件过滤分页查询
            ScoredPage<SolrItem> solrItems = solrTemplate.queryForPage(query, SolrItem.class);
            data.put("rows", solrItems.getContent());

            //设置总记录数
            data.put("total", solrItems.getTotalElements());
            //设置总页数
            data.put("totalPage", solrItems.getTotalPages());
            //把数据封装到Map上
        }
        return data;
    }

    //过滤分类方法
    private void filterCategory(Query highlightQuery,Map<String, Object> params) {
        String category = (String) params.get("category");
        if (!"".equals(category)) {
            Criteria criteria = new Criteria("category").is(category);
            highlightQuery.addCriteria(criteria);
        }
    }

    //过滤品牌方法
    private void filterBrand(Query highlightQuery,Map<String, Object> params) {
        String brand = (String) params.get("brand");
        if (!"".equals(brand)) {
            Criteria criteria = new Criteria("brand").is(brand);
            highlightQuery.addCriteria(criteria);
        }
    }

    //过滤规格方法
    private void filterSpec(Query highlightQuery,Map<String, Object> params) {
        Map<String,Object> specMap = (Map<String, Object>) params.get("spec");
        if (specMap!=null) {
            for (String key : specMap.keySet()) {
                if (StringUtils.isNoneBlank(key)) {
                    Criteria criteria = new Criteria("spec_"+key).is(specMap.get(key));
                    highlightQuery.addCriteria(criteria);
                }
            }
        }
    }

    //价格过滤方法
    private void filterPrice(Query highlightQuery,Map<String, Object> params) {
        String priceString = (String) params.get("price");
        if (StringUtils.isNoneBlank(priceString) ) {
            String[] price = priceString.split("-");
            if (!"0".equals(price[0])) {
                Criteria criteria = new Criteria("price").greaterThanEqual(price[0]);
                highlightQuery.addCriteria(criteria);
            }

            if (!"*".equals(price[1])) {
                Criteria criteria = new Criteria("price").lessThanEqual(price[1]);
                highlightQuery.addCriteria(criteria);
            }
        }
    }

    //得到当前页
    private Integer getcurrentPage(Map<String, Object> params) {
        Integer page = (Integer) params.get("page");
        if (page ==null) {
            page = 1;
        }
        return page;
    }

    //得到页大小
    private Integer getRows(Map<String, Object> params) {
        Integer rows = (Integer) params.get("rows");
        if (rows ==null) {
            rows = 20;
        }
        return rows;
    }

    //得到开始查询数
    private Integer getOffset(Integer page,Integer rows) {
        return (page - 1) * rows;
    }
    
    //设置排序条件
    private void setSort(Query highlightQuery,Map<String, Object> params) {
        //拿到要排序的字段
        String sortField = (String) params.get("sortField");
        //拿到要排序的方向(值)
        String sortValue = (String) params.get("sort");
        if (StringUtils.isNoneBlank(sortField)) {
            Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue)? Sort.Direction.ASC: Sort.Direction.DESC,sortField);
            highlightQuery.addSort(sort);
        }
    }

    //保存或更新方法
    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    @Override
    public void delete(List<Long> goodsIds) {
        //先创建查询对象
        Query query = new SimpleQuery();
        //创建条件对象
        Criteria criteria = new Criteria("goodsId").in(goodsIds);
        //添加条件
        query.addCriteria(criteria);
        //根据查询对象删除
        UpdateResponse updateResponse = solrTemplate.delete(query);

        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }

    }

}
