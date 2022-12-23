package com.losca.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 15:50:57
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
