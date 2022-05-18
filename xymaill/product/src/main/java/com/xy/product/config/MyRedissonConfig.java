package com.xy.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    /**
     * 所有redisson 使用  都要使用redisson client
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        // 默认连接地址 127.0.0.1:6379
        // RedissonClient redisson = Redisson.create();
        //1.创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.31.53:6379");
        //2.根据config创建出redissonClient示例

        config.useSingleServer().setConnectionMinimumIdleSize(5);
        config.useSingleServer().setPassword("xy123456");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
