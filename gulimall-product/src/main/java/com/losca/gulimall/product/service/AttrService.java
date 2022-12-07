package com.losca.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:51:00
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

