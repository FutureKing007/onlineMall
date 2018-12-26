package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.cart.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

@Service(interfaceName = "GoodsService")
@Transactional
public class GoodsServiceimpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void save(Goods goods) {
        try {
            //往tb_goods表插入一条good的数据
            goodsMapper.insertSelective(goods);
            GoodsDesc goodsDesc = goods.getGoodsDesc();
            // 往tb_goods_desc表插入一条数据
            goodsDesc.setGoodsId(goods.getId());
            //查询数据
            goodsDescMapper.insertSelective(goodsDesc);
            //遍历goods里面封装的item
            if ("1".equals(goods.getIsEnableSpec())) {
                for (Item item : goods.getItems()) {
                    // 创建StringBuilder用来拼接SKU表的名字
                    StringBuilder title = new StringBuilder();
                    title.append(goods.getGoodsName());
                    Map<String,Object> spec= JSON.parseObject(item.getSpec(), Map.class);
                    for (Object value : spec.values()) {
                        title.append(" " + value);
                    }
                    // 为Item设置标题
                    item.setTitle(title.toString());

                    // 设置Item属性
                    setItemInfo(item,goods);
                    //往数据库插入数据
                    itemMapper.insertSelective(item);
                }
            } else {
//                创建SKU具体商品对象
                Item item = new Item();
//                设置商品的价格
                item.setTitle(goods.getGoodsName());
//                设置SKU商品库存
                item.setNum(9999);
                //设置SKU商品启用状态
                item.setStatus("1");
                //这是是否默认
                item.setIsDefault("1");
                //设置规格选项
                item.setSpec("{}");
//                设置价格
                item.setPrice(goods.getPrice());
                setItemInfo(item,goods);
                itemMapper.insertSelective(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setItemInfo(Item item,Goods goods) {
        //为SKU设置图片
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
        // 设置SKU商品的分类(三级分类)
        item.setCategoryid(goods.getCategory3Id());
        // 设置SKU商品创建时间
        item.setCreateTime(new Date());
        // 设置SKU商品的修改时间
        item.setUpdateTime(item.getCreateTime());
        item.setGoodsId(goods.getId());
        item.setSellerId(goods.getSellerId());
        //先用itemCatMapper根据Category3Id 去数据库找到相应的类别名,然后设置类别名
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
        // 先用brandMapper根据goods封装好的id去数据库找相应的品牌名,然后设置品牌名
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getBrandId()).getName());
        // 先用sellerMapper 根据goods封装好的Id去找商家的名字
        item.setSeller(sellerMapper.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }

    @Override
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        PageInfo<Map<String,Object>> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                goodsMapper.findAll(goods);
            }
        });
        List<Map<String, Object>> pageInfoList = pageInfo.getList();
        for(Map<String,Object> map:pageInfoList){
            ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
            map.put("category1Name",itemCat1.getName() != null ? itemCat1.getName() : "");

            ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
            map.put("category2Name",itemCat2.getName() != null ? itemCat2.getName() : "");

            ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
            map.put("category3Name",itemCat3.getName() != null ? itemCat3.getName() : "");
        }
        return new PageResult(pageInfo.getTotal(),pageInfoList);
    }

    @Override
    public void updateStatus(String columnName, Serializable[] ids, String status) {
        try {
            goodsMapper.updateStatus(columnName, ids, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getGoods(Serializable goodsId) {
        //创建dataModel作为返回对象
        Map<String, Object> dataModel = new HashMap<>();
        //根据goodsId查到goods (tb_goods)
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        //根据goodsId查到goodsDesc (tb_goods_desc)
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

        dataModel.put("goods", goods);
        dataModel.put("goodsDesc", goodsDesc);

        if (goods != null && goods.getCategory3Id() != null) {
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);
        }

        //根据goodsId查询Items (tb_item)
        //创建Example对象
        Example example = new Example(Item.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
        //设置条件
        criteria.andEqualTo("goodsId", goodsId);
        criteria.andEqualTo("status", 1);
        //设置排序
        example.orderBy("isDefault").desc();
        //执行查询
        List<Item> itemList = itemMapper.selectByExample(example);
        //放进dataModel
        dataModel.put("itemList", JSON.toJSONString(itemList));
        //返回数据
        return dataModel;
    }

    @Override
    public List<Item> findItemByGoodsId(Serializable[] ids) {
        try {
            Example example = new Example(Item.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("goodsId", Arrays.asList(ids));
            return itemMapper.selectByExample(example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
