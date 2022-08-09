package com.xy.product.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
//特别注意 队列一定是点 交换机是一个单词 所以无所谓 如果队列不是点不会报错 但是收不到数据
public class MyRabbitProductUseConfig {


    //这个是死信队列 ，不能有任何消费者监听消息，目的是为了 这个队列的内容大于队列过期时间 就会将这个队列的内容转到 定时交换机中
    @Bean
    public Queue orderDelayQueue() {
        //此队列要为死信队列 需要设置过期时间 所有要设置 argument
        Map<String,Object> argument = new HashMap<>();
        //设置 到时间后转到哪个exchange
        argument.put("x-dead-letter-exchange","order-event-exchange");
        //设置 到时间后转到哪个路由键
        argument.put("x-dead-letter-routing-key","order.release.order");
        //设置 队列过期时间 以毫秒为单位的
        argument.put("x-message-ttl",10000);
        //String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        return new Queue("order.delay.queue",Boolean.TRUE,Boolean.FALSE,Boolean.FALSE,argument);
    }

    //会从定时交换机接收数据并交到监听方法中
    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue",Boolean.TRUE,Boolean.FALSE,Boolean.FALSE);
    }

    //消息 会先发到orderEventExchange 交换机中，然后会根据对应的路邮件发送到orderDelayQueue 中，等时间一到 再将新的消息 新的路邮件发送到
   // 会先发到orderEventExchange 交换机中，此时交换机会根据新的路邮件 到新的队列orderReleaseOrderQueue 并交给监听处理
    @Bean
    public Exchange orderEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange",Boolean.TRUE,Boolean.FALSE);
    }

    //此绑定 是把 orderEventExchange 和 orderDelayQueue 绑定
    @Bean
    public Binding orderDelayBinding() {
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.delay.order",
                null);
    }

    //此绑定 是把 orderEventExchange 和 orderReleaseOrderQueue 绑定
    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }
}
