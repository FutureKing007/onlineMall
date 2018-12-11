package com.pinyougou.mapper;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

//继承Mapper,里面有CRUD 的方法
public interface BrandMapper extends Mapper<Brand> {
    List<Brand> findAll(Brand brand);

    @Select("SELECT id,name text from tb_brand")
    List<Map<String,Object>> findAllByIdAndName();
    /*查询全部品牌*/

}
