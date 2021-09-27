package com.xy.product.web;

import com.alibaba.nacos.common.util.UuidUtils;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.service.CategoryService;
import com.xy.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class TestController {



    @GetMapping({"/test"})
    public String indexPage(Model model) {
        return "test";
    }

}
