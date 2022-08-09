package com.xy.product.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.product.entity.BrandEntity;
import com.xy.product.entity.Test1Entity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;


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

    /**
     * spring boot 整合 rabbitmq 实际不这么用 实际请 参考 config下的rabbitproductuse 内容
     * 倒入pom <dependency>
     *             <groupId>org.springframework.boot</groupId>
     *             <artifactId>spring-boot-starter-amqp</artifactId>
     * </dependency>
     *
     * 开启 @EnableRabbit
     *
     *
     * 给配置文件配置rabbitmq 配置：spring
     * rabbitmq:
     *     host: 192.168.31.53
     *     port: 5672
     *     virtual-host: /
     *     username: guest
     *     password: guest
     *
     *     配置json序列化
     *
     * 如果创建 exange queue 并绑定关系
     *
     */


    //create exahge by AmqpAdmin
    @Test
    public void testRabbitMqCreateExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        DirectExchange directExchange = new DirectExchange("xyExchange",Boolean.TRUE,Boolean.FALSE);
        amqpAdmin.declareExchange(directExchange);
    }

    @Test
    public void testRabbitMqCreateQueue() {
        //exclusive 是否是排他 意思就是只有一个人能使用，实际开发中 不应该是排他，每个人都可以连接队列
        //durable 是否持久化 关了rabbit 消息 是否存在
        //String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        Queue queue = new Queue("xyQueue",Boolean.TRUE,Boolean.FALSE,Boolean.FALSE);
        amqpAdmin.declareQueue(queue);
    }

    @Test
    public void testRabbitMqCreateBinging() {
        //String destination 目的地, Binding.DestinationType destinationType 目的地类型, String exchange 交换机名称, String routingKey 路由建, @Nullable Map<String, Object> arguments
        Binding binding = new Binding("xyQueue",Binding.DestinationType.QUEUE,"xyExchange","xyRouteKey",null);
        amqpAdmin.declareBinding(binding);
    }


}
