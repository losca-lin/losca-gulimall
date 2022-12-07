package com.losca.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 15:50:57
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

