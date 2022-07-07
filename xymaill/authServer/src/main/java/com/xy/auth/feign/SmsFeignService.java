package com.xy.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("nacos-maill-thirdParty")
public interface SmsFeignService {

    @GetMapping("/maillThirdParty/sendSms")
    public void sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
