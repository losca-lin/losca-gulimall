package com.losca.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.losca.common.utils.PageUtils;
import com.losca.common.utils.R;
import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author losca
 * @email 783840358@qq.com
 * @date 2022-12-07 10:50:59
 */
public interface CategoryService extends IService<CategoryEntity> {


    Map<String, List<Catelog2Vo>> getCatalogJsonWithLocal2();

    Map<String, List<Catelog2Vo>> getCatalogJsonWithLocal();

    PageUtils queryPage(Map<String, Object> params);

    R listTree();

    void removeMenuByIds(List<Long> catIds);

    Long[] findCatelogPath(Long catelogId);

    List<CategoryEntity> getLevel1Categories();

    Map<String, List<Catelog2Vo>> getCatalogJson();

    Map<String, List<Catelog2Vo>> getCatalogJsonWithRedisson();

    void updateCascade(CategoryEntity category);
}

