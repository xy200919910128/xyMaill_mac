package com.xy.auth.service;

import com.xy.auth.feign.MemberFeignService;
import com.xy.auth.feign.SmsFeignService;
import com.xy.common.exception.BizCode;
import com.xy.common.to.UserRegistVo;
import com.xy.common.utils.R;
import com.xy.common.utils.RandomCodeUtils;
import com.xy.common.utils.RedisKeyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegistServiceImpl implements  RegistService{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public R regist(UserRegistVo userRegistVo) {
        String code = stringRedisTemplate.opsForValue().get(RedisKeyConstant.smsRedisKey+userRegistVo.getPhone());
        if(StringUtils.isNotBlank(code)){
            String[] codeAndDate = code.split("_");
            if(StringUtils.equals(codeAndDate[0],userRegistVo.getCode())){
                //调用注册
                stringRedisTemplate.delete(RedisKeyConstant.smsRedisKey+userRegistVo.getPhone());
                memberFeignService.regist(userRegistVo);
            }
        }
        return R.ok();
    }

    @Override
    public R login(UserRegistVo userRegistVo) {
        return null;
    }
}
