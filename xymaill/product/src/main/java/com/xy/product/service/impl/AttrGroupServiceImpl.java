package com.xy.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.AttrAttrgroupRelationDao;
import com.xy.product.dao.AttrGroupDao;
import com.xy.product.entity.*;
import com.xy.product.service.AttrAttrgroupRelationService;
import com.xy.product.service.AttrGroupService;
import com.xy.product.service.AttrService;
import com.xy.product.service.ProductAttrValueService;
import com.xy.product.vo.AttrGroupWithAttrsVo;
import com.xy.product.vo.AttrIdVoEntity;
import com.xy.product.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrgroupRelationService;

    @Autowired
    private AttrAttrgroupRelationDao attrgroupRelationDao;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCatId(Map<String, Object> params, Long catId) {
        //最后想要的sql
        //select * from pms_attr_group where catelog_id = catId and (attr_group_id = key or attr_group_name like '%key%')
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        //查询全部
        if(catId!=0){
            wrapper.eq("catelog_id",catId); //key的是数据库的catelog_id
        }
        if(params.get("key")!=null){
            if(StringUtils.isNotEmpty(params.get("key").toString())){
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",params.get("key").toString()).or().like("attr_group_name",params.get("key").toString());
                });
            }
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> queryAttrRelation(Long attrgroupId) {
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<AttrAttrgroupRelationEntity>();
        if(attrgroupId!=0){
            wrapper.eq("attr_group_id",attrgroupId); //key的是数据库的组和属性关系的attr_group_id
        }
        List<AttrAttrgroupRelationEntity> AttrAttrgroupRelationEntityLists = attrgroupRelationService.list(wrapper);
        if(AttrAttrgroupRelationEntityLists!=null){
            List<Long> ids = AttrAttrgroupRelationEntityLists.stream().map((AttrAttrgroupRelationEntity attrAttrgroupRelationEntity)->{
                return attrAttrgroupRelationEntity.getAttrId();
            }).collect(Collectors.toList());
            List<AttrEntity> list = (List<AttrEntity>) attrService.listByIds(ids);
            return list;
        }
        return null;
    }

    @Override
    public void delete(List<AttrIdVoEntity> attrIdVoEntityList) {
        attrgroupRelationDao.batchDelete(attrIdVoEntityList);
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //com.atguigu.gulimall.product.vo
        //1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group,attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());

        return collect;
    }


    @Override
    public List<SkuInfoVo.BaseSpuAttrVo> getAttrGroupWithAttrsBySkuInfoEntity(SkuInfoEntity skuInfoEntity) {
        List<SkuInfoVo.BaseSpuAttrVo> baseSpuAttrVoList = null;
        Map<Long, SkuInfoVo.BaseAttrVo> attrIdAndBaseAttrVoMap = null;
        if(skuInfoEntity!=null){
            Long spuId = skuInfoEntity.getSpuId();
            Long catalogId = skuInfoEntity.getCatalogId();

            LambdaQueryWrapper<AttrGroupEntity> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(catalogId!=null,AttrGroupEntity::getCatelogId,catalogId);
            //get 分组信息
            List<AttrGroupEntity> attrGroupEntityList = this.list(lambdaQueryWrapper);
            if(!CollectionUtils.isEmpty(attrGroupEntityList)){
                //根据spu id 获得spu的所有信息
                List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.list(new LambdaQueryWrapper<ProductAttrValueEntity>().eq(spuId!=null,ProductAttrValueEntity::getSpuId,spuId));
                if(!CollectionUtils.isEmpty(productAttrValueEntityList)){
                    //将 productAttrValueEntityList 分组 并返回 Map<Long, SkuInfoVo.BaseAttrVo>
                    attrIdAndBaseAttrVoMap = productAttrValueEntityList.stream().collect(Collectors.groupingBy(ProductAttrValueEntity::getAttrId)).
                            entrySet().stream().collect(Collectors.toMap((pro)->pro.getKey(),(pro)->{
                        SkuInfoVo.BaseAttrVo baseAttrVo = new SkuInfoVo.BaseAttrVo();
                        baseAttrVo.setAttrId(pro.getKey());
                        List<String> attrName = pro.getValue().stream().limit(1).map(productAttrValueEntity -> productAttrValueEntity.getAttrName()).collect(Collectors.toList());;
                        List<String> attrValues = pro.getValue().stream().map(productAttrValueEntity -> productAttrValueEntity.getAttrValue()).collect(Collectors.toList());;
                        baseAttrVo.setAttrName(attrName.get(0));
                        baseAttrVo.setAttrValues(attrValues);
                        return baseAttrVo;
                    },(k1,k2)->k1));

                }
                //获取 分组信息 并返回 Map<Long,String> attrGroupMap
                Map<Long,String> attrGroupMap = attrGroupEntityList.stream().collect(
                        Collectors.toMap(AttrGroupEntity::getAttrGroupId,
                                AttrGroupEntity::getAttrGroupName,
                                (k1,k2)->{return k1;})
                );
                //查询当前分组下有哪些规格属性 id
                List<Long> groupIdList = attrGroupEntityList.stream().map((attrGroupEntity)->{
                    return attrGroupEntity.getAttrGroupId();
                }).distinct().collect(Collectors.toList());
                List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList =  attrgroupRelationService.getAttrgroupRelationListBygroupIdList(groupIdList);

                //将Map<Long, SkuInfoVo.BaseAttrVo> 转成list
                final List<SkuInfoVo.BaseAttrVo> baseAttrVoList =  attrIdAndBaseAttrVoMap.entrySet().stream().map((attrMap)->{
                    return   attrMap.getValue();
                }).collect(Collectors.toList());

                if(!CollectionUtils.isEmpty(attrAttrgroupRelationEntityList)){
                    baseSpuAttrVoList =  attrAttrgroupRelationEntityList.stream().map((AttrAttrgroupRelationEntity attrAttrgroupRelationEntity)->{
                        SkuInfoVo.BaseSpuAttrVo baseSpuAttrVo = new SkuInfoVo.BaseSpuAttrVo();
                        baseSpuAttrVo.setGroupName(attrGroupMap.get(attrAttrgroupRelationEntity.getAttrGroupId()));
                        baseSpuAttrVo.setBaseAttrVoList(baseAttrVoList.stream().filter((baseAttrVo)->{
                            return baseAttrVo.getAttrId().longValue()==attrAttrgroupRelationEntity.getAttrId().longValue();
                        }).collect(Collectors.toList()));
                        return baseSpuAttrVo;
                    }).collect(Collectors.toList());
                }
            }
        }
        return baseSpuAttrVoList;
    }

}