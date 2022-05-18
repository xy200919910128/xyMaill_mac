package com.xy.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.product.entity.SpuInfoDescEntity;
import com.xy.product.entity.Test1Entity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu信息介绍
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:37
 */
@Mapper
public interface Test1Dao extends BaseMapper<Test1Entity> {

    public List<Test1Entity> getCanSearch1(@Param("attrIds") List<Long> ids);

}
