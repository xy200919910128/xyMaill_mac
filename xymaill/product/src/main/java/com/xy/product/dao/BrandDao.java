package com.xy.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.product.entity.BrandEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
