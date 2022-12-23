package com.losca.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.order.entity.OrderReturnApplyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退货申请
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 15:50:57
 */
@Mapper
public interface OrderReturnApplyDao extends BaseMapper<OrderReturnApplyEntity> {
	
}
