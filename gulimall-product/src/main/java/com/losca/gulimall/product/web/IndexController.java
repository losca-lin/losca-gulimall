package com.losca.gulimall.product.web;

import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.CategoryService;
import com.losca.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    //index/json/catalog.json
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;

    }
}
