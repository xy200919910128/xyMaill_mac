package com.xy.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.SkuSaleAttrValueDao;
import com.xy.product.entity.SkuInfoEntity;
import com.xy.product.entity.SkuSaleAttrValueEntity;
import com.xy.product.service.SkuInfoService;
import com.xy.product.service.SkuSaleAttrValueService;
import com.xy.product.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuSaleAttrValueEntity> getSkuSaleAttrValueEntityBySkuIdList(List<Long> skuIdList){
        LambdaQueryWrapper<SkuSaleAttrValueEntity> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(skuIdList),SkuSaleAttrValueEntity::getSkuId,skuIdList);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<SkuInfoVo.SalesAttrVo> getSalesAttrVoList(SkuInfoEntity skuInfoEntity) {
        List<SkuInfoVo.SalesAttrVo> salesAttrVoList = null;
        if(skuInfoEntity!=null){
            Long spuId  = skuInfoEntity.getSpuId();
            List<SkuInfoEntity> skuInfoEntityList =  skuInfoService.getSkusBySpuId(spuId);
            if(!CollectionUtils.isEmpty(skuInfoEntityList)) {
              List<Long> skuIdList  =  skuInfoEntityList.stream().map(sku->sku.getSkuId()).collect(Collectors.toList());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList =   this.getSkuSaleAttrValueEntityBySkuIdList(skuIdList);
                if(!CollectionUtils.isEmpty(skuSaleAttrValueEntityList)){
                  Map<Long,List<SkuSaleAttrValueEntity>> SkuSaleAttrValueEntityGroup =  skuSaleAttrValueEntityList.stream().collect(Collectors.groupingBy(SkuSaleAttrValueEntity::getAttrId));
                    salesAttrVoList = SkuSaleAttrValueEntityGroup.entrySet().stream().map((skuMap)->{
                          SkuInfoVo.SalesAttrVo salesAttrVo = new SkuInfoVo.SalesAttrVo();
                          salesAttrVo.setAttrId(skuMap.getKey());
                          salesAttrVo.setAttrName(skuMap.getValue().get(0).getAttrName());
                          salesAttrVo.setAttrValuesWithSkuIdsList(skuMap.getValue().stream().map((attrValueEntity)->{
                              SkuInfoVo.AttrValuesWithSkuIds attrValuesWithSkuIds = new SkuInfoVo.AttrValuesWithSkuIds();
                              attrValuesWithSkuIds.setAttrValue(attrValueEntity.getAttrValue());
                              attrValuesWithSkuIds.setSkuIds(skuMap.getValue().stream().filter((saleAttrValueEntity)->{
                                  return saleAttrValueEntity.getSkuId().longValue()==attrValueEntity.getSkuId().longValue();
                              }).map((valueEntity)->{return valueEntity.getSkuId();}).collect(Collectors.toList()));
                              return attrValuesWithSkuIds;
                          }).collect(Collectors.toList()));

                          return salesAttrVo;
                  }).collect(Collectors.toList());

                    salesAttrVoList.stream().forEach(System.out::println);

                    salesAttrVoList.stream().forEach((salesAttrVo)->{
                        Map<String,List<SkuInfoVo.AttrValuesWithSkuIds>> stringListMap = salesAttrVo.getAttrValuesWithSkuIdsList().stream().collect(Collectors.groupingBy(attrValuesWithSkuIds -> attrValuesWithSkuIds.getAttrValue()));
                        List<SkuInfoVo.AttrValuesWithSkuIds> list = stringListMap.entrySet().stream().map((skuAttrIdsMap)->{
                            SkuInfoVo.AttrValuesWithSkuIds skuIds = new SkuInfoVo.AttrValuesWithSkuIds();
                            skuIds.setAttrValue(skuAttrIdsMap.getKey());
                            skuIds.setSkuIds(skuAttrIdsMap.getValue().stream().map((attrValuesWithSkuIds)->{return attrValuesWithSkuIds.getSkuIds().get(0).longValue();}).collect(Collectors.toList()));
                            return skuIds;
                        }).collect(Collectors.toList());
                        salesAttrVo.setAttrValuesWithSkuIdsList(list);
                    });
                }
            }
        }
        return salesAttrVoList;
    }

}