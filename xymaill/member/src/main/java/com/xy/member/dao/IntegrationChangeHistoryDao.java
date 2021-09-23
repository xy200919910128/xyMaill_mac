package com.xy.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.member.entity.IntegrationChangeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分变化历史记录
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 14:00:33
 */
@Mapper
public interface IntegrationChangeHistoryDao extends BaseMapper<IntegrationChangeHistoryEntity> {
	
}
