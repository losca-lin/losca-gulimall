package com.losca.gulimall.product.web;

import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String index(ModelMap map) {
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        map.addAttribute("categories", categorys);
        return "index";
    }
}
