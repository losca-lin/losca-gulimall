package com.losca.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 15:50:57
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

