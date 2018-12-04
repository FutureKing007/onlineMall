package com.pinyougou.mapper;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
//继承Mapper,里面有CRUD 的方法
public interface BrandMapper extends Mapper<Brand> {
    /*查询全部品牌*/

}
