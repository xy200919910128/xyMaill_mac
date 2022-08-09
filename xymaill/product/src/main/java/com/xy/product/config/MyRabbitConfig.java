package com.xy.product.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

     @Bean
     public MessageConverter messageConverter() {
         Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
         return messageConverter;
     }

     //可以判断 消息是否到达mq服务器
    //需要在xml配置：publisher-confirms: true
    @PostConstruct //方法初始化完成就会运行PostConstruct
    public void initConfirmCallback() {
         rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
             @Override
             /**
              * correlationData 消息的唯一id
              * b 是否成功到达rabbit 服务器
              * s 错误的数据原因
              */
             public void confirm(CorrelationData correlationData, boolean b, String s) {
             }
         });
     }


     //判断消息时候从交换机到达队列 到达队列不会回调 否则就能回调
     public void initReturnCallBack () {
         rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
             /**
              * message 那个消息投递失败
              * i 回复的状态吗
              * s 回复的文本内容
              * s1：发送到的哪个交换机
              * s2：用的哪个路邮件
              */
             @Override
             public void returnedMessage(Message message, int i, String s, String s1, String s2) {

             }
         });
     }

}
