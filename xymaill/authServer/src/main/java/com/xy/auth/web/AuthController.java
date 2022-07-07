package com.xy.auth.web;


import com.xy.auth.entity.User;
import com.xy.auth.feign.SmsFeignService;
import com.xy.auth.service.SendCodeService;
import com.xy.common.utils.R;
import com.xy.common.utils.RandomCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SendCodeService sendCodeService;



    @GetMapping("/sendSms")
    public R sendSms(@RequestParam("phone") String phone) {
        return sendCodeService.sendMsg(phone);
    }

}
