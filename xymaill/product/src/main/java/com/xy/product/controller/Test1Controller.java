package com.xy.product.controller;

import com.xy.common.utils.PageUtils;
import com.xy.common.utils.R;
import com.xy.product.entity.BrandEntity;
import com.xy.product.entity.ProductAttrValueEntity;
import com.xy.product.entity.Test1Entity;
import com.xy.product.service.AttrService;
import com.xy.product.service.ProductAttrValueService;
import com.xy.product.service.Test1Service;
import com.xy.product.vo.AttrResponseVoEntity;
import com.xy.product.vo.AttrVoEntity;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Rabbitmq 测试类
 *
 * @author xy
 * @email xy@gmail.com
 * @date 2020-08-10 10:37:38
 */
@RestController
@RequestMapping("test1")
public class Test1Controller {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 如果接受list 直接用 @RabbitListener(queues = {"xyQueue"}) 监听方法吧
     */
    @GetMapping(value = "/sendMessage")
    public void sendMessage() {
        //rabbitTemplate.convertAndSend("xyExchange","xyRouteKey","hello xy");
        for(int i = 0; i < 10; i++) {
            BrandEntity brandEntity = new BrandEntity();
            brandEntity.setBrandId(1l);
            brandEntity.setName("xy23");
            List arrays = Arrays.asList(brandEntity,brandEntity);
            if(i%2==0){
                //发送的时候可以给每个消息一个唯一标识：CorrelationData
                rabbitTemplate.convertAndSend("xyExchange","xyRouteKey",brandEntity,new CorrelationData(i+""));
            }else {
                rabbitTemplate.convertAndSend("xyExchange","xyRouteKey", arrays,new CorrelationData(i+""));
            }
        }
    }


    //延时队列：原理 ：
    //给一个消息对列设置过期时间，然后将到时间的消息转到另一个死信队列里 有程序专门处理死信队列的内容即可。可以理解他是一个定时器 优先使用这个 给队列设置过期时间
    //给一个消息设置过期时间：剩下的同上 不用这个，因为mq 是惰性检查 如果判断 第一个 的过期时间 只有第一个真正过期了 才会检查后面的

    @GetMapping(value = "/sendDelayOrder")
    public String sendDelayOrder() throws Exception{
        for(int i = 0; i < 10; i++) {
            BrandEntity brandEntity = new BrandEntity();
            brandEntity.setName("xy"+i);
            rabbitTemplate.convertAndSend("order-event-exchange","order.delay.order",brandEntity);
            Thread.sleep(1000);
        }
        return "ok";
    }

}
