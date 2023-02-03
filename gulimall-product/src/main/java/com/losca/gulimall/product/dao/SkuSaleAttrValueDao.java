package com.losca.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.losca.gulimall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);
}
