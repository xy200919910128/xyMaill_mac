package com.xy.auth.web;


import com.xy.auth.service.RegistService;
import com.xy.auth.service.SendCodeService;
import com.xy.common.to.UserRegistVo;
import com.xy.common.utils.R;
import com.xy.common.utils.RedisKeyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SendCodeService sendCodeService;
    @Autowired
    private RegistService registService;

    @GetMapping("/sendSms")
    public R sendSms(@RequestParam("phone") String phone) {
        return sendCodeService.sendMsg(phone);
    }

    @PostMapping("/regist")
    public RedirectView regist(@Valid UserRegistVo userRegistVo, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session, HttpRequest req){
        if(bindingResult.hasErrors()){
            RedirectView redirectView = new RedirectView();
            Map<String,Object> errMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errMap",errMap);
            redirectView.setUrl("http://auth.xymaill.com:8013/reg");
            return redirectView;
        }
        session.setAttribute("aaa",1);
        //registService.regist(userRegistVo);
        return new RedirectView("http://auth.xymaill.com:8013/login");
    }
}
