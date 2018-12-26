package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.cart.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "SpecificationService")
@Transactional
public class SpecificationServiceimpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;


    @Override

    public PageResult findByPage(Specification specification, Integer page, Integer rows) {
        try {
            PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    specificationMapper.findAll(specification);
                }
            });

            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void save(Specification specification) {
        try {
            specificationMapper.insertSelective(specification);
            specificationOptionMapper.save(specification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpecificationOption> findSpecOption(Long id) {

        try {
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(id);
            return specificationOptionMapper.select(so);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Specification specification) {
        try {
            specificationMapper.updateByPrimaryKeySelective(specification);
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(specification.getId());
            specificationOptionMapper.delete(so);
            specificationOptionMapper.save(specification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            Example example = new Example(Specification.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id",Arrays.asList(ids));
            specificationMapper.deleteByExample(example);
            for (Serializable id : ids) {
                SpecificationOption so = new SpecificationOption();
                so.setSpecId((Long) id);
                specificationOptionMapper.delete(so);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        return specificationMapper.findAllByIdAndName();
    }
}
