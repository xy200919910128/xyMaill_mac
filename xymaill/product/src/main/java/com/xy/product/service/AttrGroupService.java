package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.product.entity.AttrEntity;
import com.xy.product.entity.AttrGroupEntity;
import com.xy.product.entity.SkuInfoEntity;
import com.xy.product.vo.AttrGroupWithAttrsVo;
import com.xy.product.vo.AttrIdVoEntity;
import com.xy.product.vo.SkuInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCatId(Map<String, Object> params, Long catId);

    List<AttrEntity> queryAttrRelation(Long attrgroupId);

    void delete(List<AttrIdVoEntity> attrIdVoEntityList);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SkuInfoVo.BaseSpuAttrVo> getAttrGroupWithAttrsBySkuInfoEntity(SkuInfoEntity skuInfoEntity);
}

