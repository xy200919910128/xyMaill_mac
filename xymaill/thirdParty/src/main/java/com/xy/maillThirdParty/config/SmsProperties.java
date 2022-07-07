/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.xy.maillThirdParty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/10/25
 **/
@Data
@ConfigurationProperties(prefix = "com.xy.sms")
public class SmsProperties {
    private String host;
    private String path;
    private String method;
    private String appcode;
}
