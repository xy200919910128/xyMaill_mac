package com.xy.search.controller;

import com.xy.common.exception.BizCode;
import com.xy.common.to.es.SkuEsModel;
import com.xy.common.utils.R;
import com.xy.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/search/save")
@RestController
@Slf4j
public class ElasticSearchSaveController {
    @Autowired
    ProductSaveService productSaveService;
    /**
     * 上架商品
     * @param skuEsModels
     * @return
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        }catch (Exception e){
            log.error("ElasticSaveController.productStatusUp商品上架错误：{}",e);
            return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(),BizCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(!b){return R.ok();}
        else {
            return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(),BizCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

}
