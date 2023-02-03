package com.losca.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

