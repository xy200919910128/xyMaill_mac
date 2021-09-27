package com.xy.search.web;

import com.xy.search.service.ProductSearchService;
import com.xy.search.vo.SearchParam;
import com.xy.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class TestLoginController {

    @GetMapping("/testLogin.html")
    public String listPage(){
        return "testLogin";
    }
}
