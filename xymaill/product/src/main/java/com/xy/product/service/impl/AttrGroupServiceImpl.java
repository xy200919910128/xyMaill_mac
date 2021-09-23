package com.xy.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.AttrAttrgroupRelationDao;
import com.xy.product.dao.AttrGroupDao;
import com.xy.product.entity.AttrAttrgroupRelationEntity;
import com.xy.product.entity.AttrEntity;
import com.xy.product.entity.AttrGroupEntity;
import com.xy.product.service.AttrAttrgroupRelationService;
import com.xy.product.service.AttrGroupService;
import com.xy.product.service.AttrService;
import com.xy.product.vo.AttrGroupWithAttrsVo;
import com.xy.product.vo.AttrIdVoEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}