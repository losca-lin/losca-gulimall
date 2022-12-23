package com.losca.gulimall.thirdparty.controller;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class OssControllerTest {
    @Resource
    OSSClient ossClient;
    @Test
    void test(){
        System.out.println("aaa");
    }
}