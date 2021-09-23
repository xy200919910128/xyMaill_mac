package com.xy.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.to.SkuHasStckVo;
import com.xy.common.utils.PageUtils;
import com.xy.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStckVo> hasStock(List<Long> skuIds);
}

