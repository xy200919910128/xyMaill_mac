package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.product.entity.AttrAttrgroupRelationEntity;
import com.xy.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveBatch(List<AttrGroupRelationVo> vos);
    List<AttrAttrgroupRelationEntity> getAttrgroupRelationListBygroupIdList(List<Long> groupIdList);
}

