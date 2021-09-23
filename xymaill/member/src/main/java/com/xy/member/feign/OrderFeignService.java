package com.xy.member.feign;

import com.xy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "nacos-maill-order")
public interface OrderFeignService {

    @RequestMapping("/order/order/info/{id}")
    public R info(@PathVariable("id") Long id);

}
