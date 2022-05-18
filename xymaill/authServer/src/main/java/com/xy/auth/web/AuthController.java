package com.xy.auth.web;


import com.xy.auth.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AuthController {

    @GetMapping("/regist")
    public String toRegist() {
        return "reg";
    }


    @ResponseBody
    @PostMapping("/testList")
    public String testList(@RequestBody List<String> ids) {
        System.out.println(ids.get(0));
        return "bbb";
    }


    @ResponseBody
    @PostMapping("/testList1")
    public String testList1(@RequestBody List<User> users) {
        System.out.println(users.get(0).getName());
        return "bbb";
    }


}
