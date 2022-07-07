package com.xy.maillThirdParty;

import com.xy.maillThirdParty.component.SmsComponent;
import com.xy.maillThirdParty.config.SmsProperties;
import com.xy.maillThirdParty.utils.HttpUtils;
import com.xy.maillThirdParty.utils.RandomCodeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirtyTest {

    @Autowired
    private SmsComponent smsComponent;

    @Test
    public void sendCode() {
        String ram = RandomCodeUtils.getRandNumber(4);
        smsComponent.sendSms("13662028041",ram);
    }

}