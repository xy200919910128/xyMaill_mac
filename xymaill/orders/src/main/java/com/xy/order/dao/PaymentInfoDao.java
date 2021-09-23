package com.xy.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.order.entity.PaymentInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 14:23:24
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
