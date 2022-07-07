/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.xy.maillThirdParty.component;

import com.xy.maillThirdParty.config.SmsProperties;
import com.xy.maillThirdParty.utils.HttpUtils;
import com.xy.maillThirdParty.utils.RandomCodeUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/10/25
 **/
@Data
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsComponent {

    @Autowired
    private SmsProperties smsProperties;

    public void sendSms(String phone ,String code) {
        String host = smsProperties.getHost();
        String path = smsProperties.getPath();
        String method = smsProperties.getMethod();
        String appcode = smsProperties.getAppcode();
        String ram = RandomCodeUtils.getRandNumber(4);
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", "【好服务系统消息】您的验证码是#"+code+"#。如非本人操作，请忽略本短信");
        querys.put("mobile", phone);
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
