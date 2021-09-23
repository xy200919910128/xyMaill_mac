package com.xy.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.constant.ProductConstant;
import com.xy.common.to.SkuHasStckVo;
import com.xy.common.to.SkuReductionTo;
import com.xy.common.to.SpuBoundTo;
import com.xy.common.to.es.Attrs;
import com.xy.common.to.es.SkuEsModel;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.common.utils.R;
import com.xy.product.dao.SpuInfoDao;
import com.xy.product.entity.*;
import com.xy.product.feign.CouponFeignService;
import com.xy.product.feign.SearchFeignService;
import com.xy.product.feign.WareFeignService;
import com.xy.product.service.*;
import com.xy.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * //TODO 高级部分完善
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2、保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);



        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(),images);


        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);


        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }


        //5、保存当前spu对应的所有sku信息；
        List<Skus> skus = vo.getSkus();
        if(skus!=null && skus.size()>0){
            skus.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //    private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1）、sku的基本信息；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2）、sku的图片信息；pms_sku_image
                skuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的无需保存

                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    private List<Attrs> getAttrList(Long spuId){
        //查询spu的所有规格属性 因为每个sku的规格属性应该是一致的
        List<ProductAttrValueEntity> baseAttrList =  attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = new ArrayList<>();
        List<Attrs> attrList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(baseAttrList)){
            attrIds = baseAttrList.stream().map((ProductAttrValueEntity productAttrValueEntity)->{
                return productAttrValueEntity.getAttrId();
            }).collect(Collectors.toList());

            attrIds = attrService.getCanSearch(attrIds);
            Set attrIdsSet = new HashSet<>(attrIds);
            attrList = new ArrayList<>();
            attrList = baseAttrList.stream().filter((ProductAttrValueEntity productAttrValueEntity)->{
                return attrIdsSet.contains(productAttrValueEntity.getAttrId());
            }).map((ProductAttrValueEntity productAttrValueEntity)->{
                Attrs attrs = new Attrs();
                attrs.setAttrId(productAttrValueEntity.getAttrId());
                attrs.setAttrName(productAttrValueEntity.getAttrName());
                attrs.setAttrValue(productAttrValueEntity.getAttrValue());
                return attrs;
            }).collect(Collectors.toList());
        }
        return attrList;
    }

    @Override
    public void up(Long spuId) {
        //查出当前的spuId对应的sku信息，品牌的名字
        List<SkuInfoEntity> skuList = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuList.stream().map((SkuInfoEntity skuInfoEntity)->{
            return skuInfoEntity.getSkuId();
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(skuList)){
            Map<Long,Boolean> stockMap = null;
            try{
                //查询远程调用 是否有库存
                R<List<SkuHasStckVo>> sokList = wareFeignService.skusHasStock(skuIdList);
                TypeReference<List<SkuHasStckVo>> typeReference = new TypeReference<List<SkuHasStckVo>>(){};
                List<SkuHasStckVo> list =  sokList.getData(typeReference);
                stockMap = list.stream().collect(Collectors.toMap((SkuHasStckVo skuHasStckVo)->{
                    return skuHasStckVo.getSkuId();
                },(SkuHasStckVo skuHasStckVo)->{
                    return skuHasStckVo.getIsStock();
                }));
            }catch(Exception e){
                log.error("调用库存失败");
            }
            List<Attrs> attrList = getAttrList(spuId);
            Map<Long,Boolean> FinalstockMap = stockMap;
            List<SkuEsModel> upLists = skuList.stream().map((SkuInfoEntity skuInfoEntity)->{
                //组装需要的数据
                SkuEsModel skuEsModel = new SkuEsModel();
                BeanUtils.copyProperties(skuInfoEntity,skuEsModel);
                skuEsModel.setSkuPrice(skuInfoEntity.getPrice());
                skuEsModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
                //查询品牌
                BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
                skuEsModel.setBrandId(brandEntity.getBrandId());
                skuEsModel.setBrandImg(brandEntity.getLogo());
                skuEsModel.setBrandName(brandEntity.getName());
                //查询分类
                CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
                skuEsModel.setCatalogId(categoryEntity.getCatId());
                skuEsModel.setCatalogName(categoryEntity.getName());
                //查询规格属性
                skuEsModel.setAttrs(attrList);
                //查询热度评分
                skuEsModel.setHotScore(0l);
                if(FinalstockMap!=null){
                    skuEsModel.setHasStock(FinalstockMap.get(skuInfoEntity.getSkuId()));
                }else{
                    skuEsModel.setHasStock(Boolean.TRUE);
                }
                System.out.println(skuEsModel);
                return skuEsModel;
            }).collect(Collectors.toList());

            //feign调用流程
            /*
                1,构造请求数据 将请求转为json
                RequestTemplate tem = buildTemplateFromAgrs.create(argv);
                2，发送请求执行(执行成功会解码响应数据)
                executeAndDecode(tem)
                3,执行请求会有重试机制 出问题会抛异常
             */
           R r = searchFeignService.productStatusUp(upLists);
           if(r.getCode()==0){
               this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
           }else{
               //远程es调用失败
           }
        }

    }


}