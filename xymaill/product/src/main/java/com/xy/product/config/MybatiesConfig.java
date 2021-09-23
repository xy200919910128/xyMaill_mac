package com.xy.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement //开启事务
public class MybatiesConfig {

     @Bean
     public PaginationInterceptor paginationInterceptor() {
         PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
         paginationInterceptor.setOverflow(Boolean.TRUE);   //超过最大页数默认返回首页
         paginationInterceptor.setLimit(1000); //每页最多1000条
         return paginationInterceptor;
     }


}
