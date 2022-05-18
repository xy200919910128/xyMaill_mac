package com.xy.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@EnableConfigurationProperties(MyThredConfigProperties.class)  EnableConfigurationProperties 如果 没写@Component 就需要写@EnableConfigurationProperties(MyThredConfigProperties.class) 这个
@Configuration
public class MyThredConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThredConfigProperties myThredConfigProperties) {
        return new ThreadPoolExecutor(myThredConfigProperties.getCoreSize(),myThredConfigProperties.getMaxSize(),myThredConfigProperties.getKeepAlive(),
                TimeUnit.SECONDS,new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }

}
