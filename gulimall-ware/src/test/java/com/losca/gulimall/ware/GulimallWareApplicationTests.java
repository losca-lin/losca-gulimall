package com.losca.gulimall.ware;

import com.losca.gulimall.ware.entity.PurchaseEntity;
import com.losca.gulimall.ware.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
