package com.xy.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "xymaill")
@Component
@Data
public class MyThredConfigProperties {

    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAlive;

}
