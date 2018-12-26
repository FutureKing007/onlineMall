package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.Content;
import com.pinyougou.cart.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
/**
 * ContentServiceImpl 服务接口实现类
 * @date 2018-12-04 15:36:38
 * @version 1.0
 */
@Service(interfaceName = "ContentService")
@Transactional
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/** 添加方法 */
	public void save(Content content){
		try {
			contentMapper.insertSelective(content);
			redisTemplate.delete("contents");
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Content content){
		try {
			contentMapper.updateByPrimaryKeySelective(content);
			redisTemplate.delete("contents");
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			contentMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(Content.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			contentMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Content findOne(Serializable id){
		try {
			return contentMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Content> findAll(){
		try {
			return contentMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public PageResult findByPage(Content content, int page, int rows){
		try {
			PageInfo<Content> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						contentMapper.selectAll();
					}
				});
			return new PageResult(pageInfo.getTotal(),pageInfo.getList());
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Content> findContentByCategoryId(Serializable categoryId) {
		List<Content> contentList = null;
		try {
			contentList = null;
			contentList = (List<Content>) redisTemplate.boundValueOps("contents").get();
			if (contentList!=null && contentList.size()>0) {
                System.out.println("从redis中拿数据");
                return contentList;
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			//创建Example对象
			Example example = new Example(Content.class);
			//用Example对象创建Criteria 对象
			Example.Criteria criteria = example.createCriteria();
			//创建查询条件
			//categroy_id列
			criteria.andEqualTo("categoryId", categoryId);
			//status列
			criteria.andEqualTo("status","1" );
			//排序,根据sort_order的值排序
			example.orderBy("sort_order").asc();
			//返回查询后的列表
			contentList = contentMapper.selectByExample(example);
//		把内容要存入Redis
			redisTemplate.boundValueOps("contents").set(contentList);
			System.out.println("从数据库中读数据");
			return contentList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}