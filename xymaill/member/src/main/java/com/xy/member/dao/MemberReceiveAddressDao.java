package com.xy.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.member.entity.MemberReceiveAddressEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员收货地址
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 14:00:33
 */
@Mapper
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {
	
}
