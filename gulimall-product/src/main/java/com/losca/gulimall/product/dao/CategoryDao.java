package com.losca.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.losca.gulimall.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
