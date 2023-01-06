package com.losca.gulimall.search.controller;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    RestHighLevelClient client;
    @GetMapping("/")
    public String index(){
        System.out.println(client);
        return "hello";
    }
}
