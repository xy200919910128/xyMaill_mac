package com.xy.product.web;

import com.xy.product.service.CategoryService;
import com.xy.product.service.SkuInfoService;
import com.xy.product.vo.SkuInfoVo;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ItemController {

    @Autowired
    private  CategoryService categoryService;
    //@Autowired
    //private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SkuInfoService skuInfoService;


    @GetMapping({"/{skuId}.html"})
    public String indexPage(@PathVariable("skuId") Long skuId, Model model) {
        System.out.println("查询商品详情"+skuId);
        SkuInfoVo skuInfoVo = skuInfoService.item(skuId);
        return "item";
    }

}
