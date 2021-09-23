package com.xy.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.constant.ProductConstant;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.AttrAttrgroupRelationDao;
import com.xy.product.dao.AttrDao;
import com.xy.product.dao.AttrGroupDao;
import com.xy.product.entity.AttrAttrgroupRelationEntity;
import com.xy.product.entity.AttrEntity;
import com.xy.product.entity.AttrGroupEntity;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.service.AttrAttrgroupRelationService;
import com.xy.product.service.AttrGroupService;
import com.xy.product.service.AttrService;
import com.xy.product.service.CategoryService;
import com.xy.product.vo.AttrResponseVoEntity;
import com.xy.product.vo.AttrVoEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrgroupRelationService;

    @Autowired
    private AttrGroupService attrAttrgroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVoEntity attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);
        if(attrEntity.getAttrType()==1){
            AttrAttrgroupRelationEntity attrGroupEntity = new AttrAttrgroupRelationEntity();
            attrGroupEntity.setAttrGroupId(attr.getAttrGroupId());
            attrGroupEntity.setAttrId(attrEntity.getAttrId());
            attrgroupRelationService.save(attrGroupEntity);
        }
    }

    @Override
    public PageUtils queryBaseListPage(Map<String, Object> params,Long catelogId,String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equals(attrType)?1:0);
        if(catelogId!=0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = params.get("key")==null?null:params.get("key").toString();
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> attrEntityList = page.getRecords();
        List<AttrResponseVoEntity> list= attrEntityList.stream().map((AttrEntity attrEntity)->{
            AttrResponseVoEntity attrResponseVoEntity = new AttrResponseVoEntity();
            BeanUtils.copyProperties(attrEntity,attrResponseVoEntity);
            Long attrId = attrResponseVoEntity.getAttrId();
            AttrAttrgroupRelationEntity attrgroupRelationEntityEntity = attrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrId));
            if(attrgroupRelationEntityEntity!=null){
                AttrGroupEntity attrGroupEntity = attrAttrgroupService.getById(attrgroupRelationEntityEntity.getAttrGroupId());
                attrResponseVoEntity.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            CategoryEntity categoryEntity = categoryService.getById(attrResponseVoEntity.getCatelogId());
            attrResponseVoEntity.setCatelogName(categoryEntity.getName());
            return attrResponseVoEntity;
        }).collect(Collectors.toList());
        pageUtils.setList(list);
        return pageUtils;
    }

    @Override
    public AttrResponseVoEntity getByIdAndGroupAndCatPath(Long attrId) {
        AttrResponseVoEntity attrResponseVoEntity = new AttrResponseVoEntity();
        AttrEntity attrEntity = this.getById(attrId);
        if(attrEntity!=null){
          BeanUtils.copyProperties(attrEntity,attrResponseVoEntity);
        }
        attrResponseVoEntity.setCatelogPath(categoryService.getCatPathByGroupCatId(attrEntity.getCatelogId()));

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =  attrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
        if(attrAttrgroupRelationEntity!=null){
            AttrGroupEntity attrGroupEntity = attrAttrgroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
            attrResponseVoEntity.setAttrGroupId(attrGroupEntity.getAttrGroupId());
        }
        return attrResponseVoEntity;
    }

    @Override
    public void updateAllInfo(AttrVoEntity attrVoEntity) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVoEntity,attrEntity);
        this.updateById(attrEntity);
        if(attrVoEntity.getAttrGroupId()!=null){
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("attr_id",attrEntity.getAttrId());
            Integer count = attrgroupRelationService.getBaseMapper().selectCount(queryWrapper);
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVoEntity.getAttrGroupId());
            if(count==0){
                if(attrVoEntity.getAttrGroupId()!=null){
                    attrgroupRelationService.save(attrAttrgroupRelationEntity);
                }
            }else{
                if(attrVoEntity.getAttrGroupId()!=null){
                    attrgroupRelationService.update(attrAttrgroupRelationEntity,new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrAttrgroupRelationEntity.getAttrId()));
                }else{
                    attrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrAttrgroupRelationEntity.getAttrId()));
                }
            }
        }
    }


    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1、当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2、当前分组只能关联别的分组没有引用的属性
        //2.1)、当前分类下的其他分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = group.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2)、这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = groupId.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //2.3)、从当前分类的所有属性中移除这些属性；
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null && attrIds.size()>0){
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }


    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = entities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        if(attrIds == null || attrIds.size() == 0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public List<Long> getCanSearch(List<Long> attrIds) {
        return this.attrDao.getCanSearch(attrIds);
    }

}