package com.losca.gulimall.product.web;

import com.losca.gulimall.product.service.SkuInfoService;
import com.losca.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;


    @GetMapping("/{skuId}.html")

    public String skuItem(@PathVariable("skuId") Long skuId, ModelMap map) throws ExecutionException, InterruptedException {
        SkuItemVo vos = skuInfoService.item(skuId);
        map.addAttribute("item", vos);
        return "item";
    }
}
