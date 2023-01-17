package com.losca.gulimall.product.config;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyRedissonConfigTest {

    @Autowired
    RedissonClient client;
    @Test
    void redisson() {
        System.out.println(client);
    }
}