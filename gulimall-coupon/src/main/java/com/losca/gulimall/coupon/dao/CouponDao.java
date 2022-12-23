package com.losca.gulimall.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 16:03:18
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
