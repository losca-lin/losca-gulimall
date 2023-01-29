package com.losca.gulimall.search.controller;

import com.losca.gulimall.search.service.SearchService;
import com.losca.gulimall.search.vo.SearchParam;
import com.losca.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, ModelMap modelMap, HttpServletRequest request) throws IOException {
        searchParam.set_queryString(request.getQueryString());
        SearchResult searchResult = searchService.search(searchParam);
        modelMap.addAttribute("result", searchResult);
        return "list";
    }
}
