package com.xy.common.to;

import lombok.Data;

@Data
public class SkuHasStckVo {

   private Long skuId; //整单id
   private Boolean isStock;//[1,2,3,4] //合并项集合
}
