package com.losca.gulimall.ware;

import com.losca.gulimall.ware.entity.PurchaseEntity;
import com.losca.gulimall.ware.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GulimallWareApplicationTests {

    @Autowired
    private PurchaseService purchaseService;
    @Test
    void contextLoads() {
        PurchaseEntity entity = new PurchaseEntity();
        entity.setPhone("16622903881");
        purchaseService.save(entity);
    }

    @Test
    void query(){
        for (PurchaseEntity purchaseEntity : purchaseService.list()) {
            System.out.println(purchaseEntity);
        }

    }

    @Test
    void testStream(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> collect = list.stream().map((item) -> {
            return item.toString();
        }).filter(x-> !x.equals("2"))
                .map((item)-> {
                    return Integer.parseInt(item);
                }).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }

}
