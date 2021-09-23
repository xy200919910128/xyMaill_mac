package com.xy.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:37
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId,@Param("status") Integer status);
}
