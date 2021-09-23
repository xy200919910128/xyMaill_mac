package com.xy.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class MaillCrosConfigration {

    @Bean
    public CorsWebFilter getCorsWebFilter () {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        //允许任何请求头 跨域
        corsConfiguration.addAllowedHeader("*");
        //允许任何方法 跨域（get put delete 等）
        corsConfiguration.addAllowedMethod("*");
        //允许那个请求来源进行跨域 （*都允许）
        corsConfiguration.addAllowedOrigin("*");
        //允许携带所有cokkey进行跨域
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        //对哪些路径请求进行跨域配置
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new  CorsWebFilter((CorsConfigurationSource) corsConfigurationSource);
    }


}
