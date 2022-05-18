package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.product.entity.SkuInfoEntity;
import com.xy.product.entity.SkuSaleAttrValueEntity;
import com.xy.product.vo.SkuInfoVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    public List<SkuInfoVo.SalesAttrVo> getSalesAttrVoList(SkuInfoEntity skuInfoEntity);
    public List<SkuSaleAttrValueEntity> getSkuSaleAttrValueEntityBySkuIdList(List<Long> skuIdList);
}

