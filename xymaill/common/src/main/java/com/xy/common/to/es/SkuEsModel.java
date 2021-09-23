package com.xy.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/*
"properties": {
			"skuId":{
				"type": "long"
			},
			"spuId":{
				"type": "keyword"
			},
			"skuTitle":{
				"type": "text",
				"analyzer": "ik_smart"
			},
			"skuPrice":{
				"type": "keyword"
			},
			"skuImage":{
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"saleCount":{
				"type": "long"
			},
			"hasStock":{
				"type": "boolean"
			},
			"hotScore":{
				"type": "long"
			},
			"brandId":{
				"type": "long"
			},
			"catelogId":{
				"type": "long"
			},
			"brandName":{
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"brandImg":{
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"catelogName":{
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"attrs": {
				"type": "nested",        #如果这个属性中的对象需要整体查询 就要加上nested 否则可以不加  是为了后期查询正确
				"properties": {
					"attrId": {
						"type": "long"
					},
					"attrName":{
						"type": "keyword",
						"index": false,
						"doc_values": false
					},
					"attrValue":{
						"type": "keyword"
					}
				}
			}
		}
 */

@Data
public class SkuEsModel {

    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;

    private Long saleCount;//销量
    private Boolean hasStock;//是否有库存
    private Long hotScore;//热度评分
    private Long brandId;//品牌id
    private Long catalogId;//分类id
    private String brandName;//品牌名称
    private String brandImg;//品牌图片
    private String catalogName;//分类名称

    private List<Attrs> attrs;
}
