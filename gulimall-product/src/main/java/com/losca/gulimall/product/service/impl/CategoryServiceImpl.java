package com.losca.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.losca.common.utils.PageUtils;
import com.losca.common.utils.Query;
import com.losca.common.utils.R;
import com.losca.gulimall.product.dao.CategoryDao;
import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public R listTree() {
        List<CategoryEntity> list = this.list();
        //一级分类
        List<CategoryEntity> collect = list.stream().filter((categoryEntity) -> {
                    return categoryEntity.getParentCid() == 0;
                }).map((item) -> {
                    item.setChildren(getChildren(item, list));
                    return item;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() ==null ? 0:menu1.getSort()) - (menu2.getSort()==null?0: menu2.getSort());
                })
                .collect(Collectors.toList());
        return R.ok().put("data", collect);
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //检查当前删除的菜单是否被别的地方引用
        this.removeByIds(catIds);
        return;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();

        //递归查询是否还有父节点
        List<Long> parentPath = findParentPath(catelogId, paths);

        //进行一个逆序排列
        Collections.reverse(parentPath);

        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid","0"));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {

        //1、收集当前节点id
        paths.add(catelogId);

        //根据当前分类id查询信息
        CategoryEntity byId = this.getById(catelogId);
        //如果当前不是父分类
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }


    public List<CategoryEntity> getChildren(CategoryEntity item, List<CategoryEntity> list) {
        List<CategoryEntity> children = list.stream().filter(categoryEntity -> {
            return item.getCatId().equals(categoryEntity.getParentCid());
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, list));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() ==null ? 0:menu1.getSort()) - (menu2.getSort()==null?0: menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

}