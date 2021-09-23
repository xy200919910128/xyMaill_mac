package com.xy.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:xy.properties")
@ConfigurationProperties(prefix = "spring1.cache1")
@Data
public class MyProperTiesConfig {
    private  String type1 ;
}
