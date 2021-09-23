package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.*;
import com.xy.product.entity.AttrEntity;
import com.xy.product.vo.AttrResponseVoEntity;
import com.xy.product.vo.AttrVoEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVoEntity attr);

    PageUtils queryBaseListPage(Map<String, Object> params,Long catelogId,String attrType);

    AttrResponseVoEntity getByIdAndGroupAndCatPath(Long attrId);

    void updateAllInfo(AttrVoEntity attrVoEntity);

    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    public List<AttrEntity> getRelationAttr(Long attrgroupId);

    List<Long> getCanSearch(List<Long> attrIds);
}

