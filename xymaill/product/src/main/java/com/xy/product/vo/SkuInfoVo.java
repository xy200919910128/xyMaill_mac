/**
 * Copyright 2019 bejson.com
 */
package com.xy.product.vo;

import com.xy.product.entity.SkuImagesEntity;
import com.xy.product.entity.SkuInfoEntity;
import com.xy.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2019-11-26 10:50:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SkuInfoVo {

    //get sku 基本信息 pms_sku_info
    private SkuInfoEntity skuInfoEntity;
    //get sku 图片信息 pms_sku_images
    private List<SkuImagesEntity> skuImagesEntityList;
    //get spu 销售属性集合
    private List<SalesAttrVo> salesAttrVoList;
    //get  spu 的介绍 pms_spu_info_desc
    private SpuInfoDescEntity spuInfoDescEntity;
    //get spu 规格参数信息
    private  List<BaseSpuAttrVo> baseSpuAttrVoLIst;

    @Data
    public static  class  SalesAttrVo {
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;

        private List<String> attrValues;
    }

    @Data
    public static  class  BaseSpuAttrVo {
        private String groupName;
        private List<BaseAttrVo> baseAttrVoList;
    }


    @Data
    public static  class  BaseAttrVo {
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;

        private List<String> attrValues;
    }

}