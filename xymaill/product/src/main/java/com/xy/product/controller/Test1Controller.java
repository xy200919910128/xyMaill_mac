package com.xy.product.controller;

import com.xy.common.utils.PageUtils;
import com.xy.common.utils.R;
import com.xy.product.entity.ProductAttrValueEntity;
import com.xy.product.entity.Test1Entity;
import com.xy.product.service.AttrService;
import com.xy.product.service.ProductAttrValueService;
import com.xy.product.service.Test1Service;
import com.xy.product.vo.AttrResponseVoEntity;
import com.xy.product.vo.AttrVoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
@RestController
@RequestMapping("test1")
public class Test1Controller {
    @Autowired
    private Test1Service test1Service;


    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Test1Entity attr){
        test1Service.save(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Test1Entity attr){
        test1Service.updateById(attr);
        return R.ok();
    }


    @GetMapping("/test2")
    public void getTest1() {
        test1Service.getCanSearch(null);
    }

}
