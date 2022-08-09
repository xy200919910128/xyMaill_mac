package com.xy.auth.schedleTest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@EnableAsync       // 可用异步任务
//@EnableScheduling  //开启spring 定时任务注解
//@Component
@Slf4j
public class Scheld {

    @Autowired
    private String getCronValue;
    /**
     * spring boot中的 1是周一。7是周日 不支持年
     *
     * 自动任务 不应该 阻塞  。可以有3种办法：
     *
     * 1 异步编排  把 service 放在 comoleteFuture.runAsync()中 并自定义线程池
     *
     * 2，使用 spring boot的定时任务线程池 （配置类：TaskSchedulingAutoConfiguration）  设置 TaskSchedulingProperties；
     * spring.task.scheduling.pool = 5
     *
     *
     * 3,让定时任务异步执行：
     * 给想要异步类上添加@EnableAsync
     * 给想要异步执行的任务方法添加 @Async
     * 异步任务配置类：TaskExecutionAutoConfiguration
     *  设置：spring.task.execution.pool.core-size = 5
     *  spring.task.execution.pool.max-size = 50
     */

    //最后解决 用 异步加定时任务不阻塞
//    @Scheduled(cron="#{@getCronValue}")
//    public void testSch(){
//
//    }

    public static void main(String[] args){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode= bCryptPasswordEncoder.encode("123asdds");

        System.out.println("加密："+encode);

        System.out.println(bCryptPasswordEncoder.matches("123asdds",encode));
    }
}
