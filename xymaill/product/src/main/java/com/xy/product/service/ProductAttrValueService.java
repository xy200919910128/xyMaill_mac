package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public void saveProductAttr(List<ProductAttrValueEntity> collect);

    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);

    public List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId);

}

