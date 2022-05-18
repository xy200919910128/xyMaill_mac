package com.xy.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.SkuInfoDao;
import com.xy.product.entity.SkuImagesEntity;
import com.xy.product.entity.SkuInfoEntity;
import com.xy.product.entity.SpuInfoDescEntity;
import com.xy.product.service.*;
import com.xy.product.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){

            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        return this.list(queryWrapper);
    }

    @Override
    //方法需要用线程池
    public SkuInfoVo item(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture =  CompletableFuture.supplyAsync(()->{
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            //get sku 基本信息 pms_sku_info
            skuInfoVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        },threadPoolExecutor);

        CompletableFuture<Void> spuInfoDescCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res)->{
            //get  spu 的介绍
            skuInfoVo.setSpuInfoDescEntity(spuInfoDescService.getById(res.getSpuId()));
        },threadPoolExecutor);

        //get spu 销售属性集合
        CompletableFuture<Void> spuAttrVoFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((SkuInfoEntity skuInfoEntity)->{
            skuInfoVo.setBaseSpuAttrVoLIst(attrGroupService.getAttrGroupWithAttrsBySkuInfoEntity(skuInfoEntity));
        },threadPoolExecutor);

        CompletableFuture<Void> skuSalesAttrFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((SkuInfoEntity skuInfoEntity)->{
            skuInfoVo.setSalesAttrVoList(skuSaleAttrValueService.getSalesAttrVoList(skuInfoEntity));
        },threadPoolExecutor);


        CompletableFuture<Void> skuImageCompletableFuture = CompletableFuture.runAsync(()->{
            //get sku 图片信息 pms_sku_images
            QueryWrapper<SkuImagesEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("sku_id",skuId);
            skuInfoVo.setSkuImagesEntityList(skuImagesService.list(queryWrapper));
        },threadPoolExecutor);
        try {
            CompletableFuture.allOf(skuInfoEntityCompletableFuture,spuInfoDescCompletableFuture,spuAttrVoFuture,skuSalesAttrFuture,skuImageCompletableFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return skuInfoVo;
    }

}