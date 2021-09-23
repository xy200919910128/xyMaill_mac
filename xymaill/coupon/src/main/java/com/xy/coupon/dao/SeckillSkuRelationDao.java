package com.xy.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.coupon.entity.SeckillSkuRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动商品关联
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 13:15:02
 */
@Mapper
public interface SeckillSkuRelationDao extends BaseMapper<SeckillSkuRelationEntity> {
	
}
