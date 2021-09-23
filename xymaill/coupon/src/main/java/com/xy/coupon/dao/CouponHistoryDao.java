package com.xy.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.coupon.entity.CouponHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 13:15:03
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
