package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2018-12-03 17:26:00
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{

    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    @Select("SELECT id,name from tb_type_template")
    List<Map<String,Object>> findTypeIdAndNameList();
}