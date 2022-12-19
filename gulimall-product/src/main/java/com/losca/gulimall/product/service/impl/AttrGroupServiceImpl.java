package com.losca.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.losca.common.utils.PageUtils;
import com.losca.common.utils.Query;
import com.losca.gulimall.product.dao.AttrGroupDao;
import com.losca.gulimall.product.dao.CategoryDao;
import com.losca.gulimall.product.entity.AttrGroupEntity;
import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.AttrGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId,paths);
        Collections.reverse(parentPath);
        return parentPath;

    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        //根据当前分类id查询信息
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        } else {
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
            String key = (String) params.get("key");
            if (StrUtil.isNotBlank(key)) {
                wrapper.and((obj) -> {
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }

    }

}