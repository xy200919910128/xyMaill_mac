package com.xy.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * spring session 整合 redis
 * <dependency>
 *             <groupId>org.springframework.session</groupId>
 *             <artifactId>spring-session-data-redis</artifactId>
 *             <version>2.2.0.RELEASE</version>
 *         </dependency>
 *       spring
 *         session:
 *              store-type: redis
 *      开启注解：@EnableRedisHttpSession
 */

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableRedisHttpSession
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
