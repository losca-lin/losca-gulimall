package com.losca.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("seckill")
public interface SeckillFeignService {
}
