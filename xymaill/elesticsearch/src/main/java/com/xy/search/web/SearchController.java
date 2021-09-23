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
public class SearchController {

    @Autowired
    private ProductSearchService productSearchService;


    @GetMapping("/list.html")
    public String listPage(@ModelAttribute SearchParam searchParam, Model model){
        SearchResult searchResult = productSearchService.getSearchResult(searchParam);
        model.addAttribute("result",searchResult);
        return "list";
    }
}
