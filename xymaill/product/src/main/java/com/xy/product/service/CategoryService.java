package com.xy.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.common.utils.PageUtils;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> queryTree();

    Integer logicDelete(List<Long> ids);

    Long[] getCatPathByGroupCatId(Long catelogId);

    void updateDetail(CategoryEntity category);

    List<CategoryEntity> getCatLevel1();

    public Map<String, List<Catelog2Vo>> getCatalogJson();
}

