package com.xy.auth.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    //现把域提高到整个网站，不局限某一个分布式系统
    @Bean
    public CookieSerializer getCookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName("xymaill.com");
        defaultCookieSerializer.setCookieName("xySession");
        return defaultCookieSerializer;
    }

    //使用json 序列化
    @Bean
    public RedisSerializer springSessionRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}
