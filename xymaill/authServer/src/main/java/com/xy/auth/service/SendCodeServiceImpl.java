package com.xy.auth.service;

import com.xy.auth.feign.SmsFeignService;
import com.xy.common.exception.BizCode;
import com.xy.common.utils.R;
import com.xy.common.utils.RandomCodeUtils;
import com.xy.common.utils.RedisKeyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class SendCodeServiceImpl implements  SendCodeService{

    @Autowired
    private SmsFeignService smsFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public R sendMsg(String phone) {
//        String num = RandomCodeUtils.getRandNumber(4);
        String num = "1234";

        String code = stringRedisTemplate.opsForValue().get(RedisKeyConstant.smsRedisKey+phone);

        if(StringUtils.isNotBlank(code)){
            String[] codeAndDate = code.split("_");
            if(System.currentTimeMillis()-Long.valueOf(codeAndDate[1])<=60000){
                return R.error(BizCode.SMS_VALID_EXCEPTION.getCode(),BizCode.SMS_VALID_EXCEPTION.getMsg());
            }
        }
        String numWithMil= num+"_"+ System.currentTimeMillis();
        //smsFeignService.sendSms(phone, num);
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.smsRedisKey+phone,numWithMil,10, TimeUnit.MINUTES);
       return R.ok();
    }
}
