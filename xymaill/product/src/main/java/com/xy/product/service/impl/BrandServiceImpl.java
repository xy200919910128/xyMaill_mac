package com.xy.product.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.BrandDao;
import com.xy.product.entity.BrandEntity;
import com.xy.product.service.BrandService;
import com.xy.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = params.get("key").toString();
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>();
        if(StringUtils.isNotEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("brand_id",key).or().like("name",key);
            });
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

    @Override
    public void updateSelfAndRelation(BrandEntity brand) {
        this.updateById(brand);
        if(StringUtils.isNotEmpty(brand.getName())){
            categoryBrandRelationService.updateByBrandId(brand.getBrandId(),brand.getName());
            //todo
        }
    }

    //同一个队列 能被多个方法监听，但只有一个方法能使用消息，消息是用完 就没有了 注 只有一个放法的内容全部完成，才会接受下一个消息
    //message 有message 头和message 体
    //第二个参数:直接将发送的消息对象转为 对应的实体
    //信道参数

    //spring boot提供两个注解：@RabbitListener（这个方法必须开起 spring boot的@EnableRabbit） 标在类和方法上 @RabbitHandler 只能标在方法上
    //@RabbitListener 表明监听那个队列
    //@RabbitHandler 处理不同消息类型的方法
    //spring rabbit mq 是自动确认消息的，如果收到消息并处理完成 就会从 服务端移除
    //这样会有问题，一旦这个方法出现问题，消息就会丢失了 （也会被自动确认）
    //最好做到手动确认 具体就是  channel.basicAck(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);
    //如果 是手动模式 哪怕没有签收 也会被 mq 重新投递
    //签收有三种模式：channel.basicAck(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);
    //channel.basicNack(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE,Boolean.FALSE);
    //channel.basicReject(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);

    @RabbitListener(queues = {"xyQueue"})
    public void receiveMessageString(Message message, BrandEntity brandEntity, Channel channel) throws Exception{
        System.out.println(brandEntity.getName());
        //此为手动确认，如果享受用手动确认，需要改配置：listener:
        //      simple:
        //        acknowledge-mode: manual
        /**
         * 第一个参数：消息的标签
         * 第二个参数 是否批量处理 如果是批量处理 表明这个getDeliveryTag之前的所有都自动回复收到
         */
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);

        //除了回复收到还可以回复没收到
        /**
         * 第一个参数：消息的标签
         * 第二个参数 是否批量处理 如果是批量处理 表明这个getDeliveryTag之前的所有都自动回复不要
         * 第三个参数 是否需要重新入队，供别人使用 requeue 为true 就是 重新放回mq  false 这个消息就不要了
         */
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE,Boolean.FALSE);
        //basicNack 和basicReject 只有一个区别 basicNack 可以批量处理  basicReject 不能批量处理
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);
    }

    @RabbitListener(queues = {"xyQueue"})
    public void receiveMessageList(Message message, List<BrandEntity> brandEntityList, Channel channel) throws Exception {
        brandEntityList.forEach(System.out::println);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);
    }

    //监听延时队列
    @RabbitListener(queues = {"order.release.order.queue"})
    public void receiveDelayMesg(Message message,BrandEntity brandEntity,Channel channel) throws Exception{
        System.out.println("一分钟后查询到的品牌名称："+brandEntity.getName());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),Boolean.FALSE);
    }

}