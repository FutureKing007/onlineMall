package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceimpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, Integer page, Integer rows) {
        try {
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    typeTemplateMapper.findAll(typeTemplate);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    @Override
    public void udpate(TypeTemplate typeTemplate) {

        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        typeTemplateMapper.deleteByExample(example);
    }

    @Override
    public List<Map<String, Object>> findTypeIdAndNameList() {
       return typeTemplateMapper.findTypeIdAndNameList();
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        try {
            return typeTemplateMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public List<Map> findSpecByTemplateId(Serializable id) {
        try {
//            根据id先查询模板
            TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
//            得到字符串类型的规格表的
            String specIdsString = typeTemplate.getSpecIds();
//            把字符串类型的规格表转换为Map类型的集合(List)
            List<Map> specLists = JSON.parseArray(specIdsString, Map.class);
            for (Map map : specLists) {
//                先创建Specification 用作 查询
                SpecificationOption specificationOption = new SpecificationOption();
//                设置Specification 设置模板
                specificationOption.setSpecId(Long.valueOf(map.get("id").toString()));
//                  查到SpecificationOption封装到一个集合中
                List<SpecificationOption> options = specificationOptionMapper.select(specificationOption);
                map.put("options", options);
            }
            return specLists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
