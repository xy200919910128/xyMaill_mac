package com.xy.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.product.entity.Test1Entity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test1Test {

    @Autowired
    private Test1Service test1Service;

    @Autowired
    private AttrService attrService;

    @Test
    public void contextLoads() {
        Test1Entity user = new Test1Entity();
        user.setId(1l);
        user.setName("xy");
        user.setGenerate("男");
        //test1Service.save(user);
        List<Long> ids = Arrays.asList(1l,2l,3l);
        System.out.println(test1Service.getCanSearch(ids));
        //System.out.println(attrService.getCanSearch(ids));


    }

}
