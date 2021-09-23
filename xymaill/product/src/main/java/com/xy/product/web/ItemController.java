package com.xy.product.web;

import com.xy.product.service.CategoryService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping({"/{skuId}.html"})
    public String indexPage(Long skuId,Model model) {
        System.out.println("查询商品详情");
        return "item";
    }

}
