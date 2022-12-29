package com.losca.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 15:55:51
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

