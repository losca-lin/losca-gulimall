package com.losca.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.losca.common.utils.PageUtils;
import com.losca.common.utils.Query;
import com.losca.common.utils.R;
import com.losca.gulimall.product.dao.CategoryDao;
import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.CategoryService;
import com.losca.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

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
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
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
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", "0"));
    }

    /**
     * 测试分布式锁 核心使用redis的setnx指令 实际生产不推荐
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //加锁
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", Thread.currentThread().getName(), 30, TimeUnit.SECONDS);
        //获取到锁，进行业务操作,为获取到锁进行重试
        if (lock) {
            //执行完业务逻辑后得进行释放锁的操作 不能删除其他人的锁
            Map<String, List<Catelog2Vo>> result = getListMap();
            //String s = stringRedisTemplate.opsForValue().get("lock");
            //if (Thread.currentThread().getName().equals(s)){
            //    stringRedisTemplate.delete("lock");
            //}
            //获取锁和删除锁应该是一个原子操作
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            stringRedisTemplate.execute(new DefaultRedisScript<>(script,Long.class), Arrays.asList("lock"), Thread.currentThread().getName());
            return result;
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            return getCatalogJson();
        }

    }

    private Map<String, List<Catelog2Vo>> getListMap() {
        //缓存没有查询数据库,返回数据
        if (stringRedisTemplate.opsForValue().get("catalogJson") == null) {
            System.out.println("查询了数据库");
            Map<String, List<Catelog2Vo>> parentCid = getStringListMap();
            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(parentCid), 1, TimeUnit.DAYS);
            return parentCid;
        }
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        System.out.println("缓存命中了");
        return result;
    }

    /**
     * 效果和getCatalogJsonWithLocal一样，锁的都是当前对象实例，由于springboot对象组件都是单例的，对于单体引用来说可以锁住
     *
     * @return
     */
    @Override
    public synchronized Map<String, List<Catelog2Vo>> getCatalogJsonWithLocal2() {
        //缓存没有查询数据库,返回数据
        if (stringRedisTemplate.opsForValue().get("catalogJson") == null) {
            System.out.println("查询了数据库");
            Map<String, List<Catelog2Vo>> parentCid = getStringListMap();
            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(parentCid), 1, TimeUnit.DAYS);
            return parentCid;
        }
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        System.out.println("缓存命中了");
        return result;
    }

    /**
     * 从数据库查询并封装数据::本地锁
     * 缓存穿透：查询了缓存没有的数据，全去查询数据库，数据库也无此记录  缓存增加空结果，设置短期的过期时间
     * 缓存雪崩：缓存到同一时间全部失效，涌入大量请求查询数据库，数据库压力过重雪崩   缓存时间设置随机值，防止缓存集体失效
     * 缓存击穿：热点数据缓存失效，请求全部打到数据库    热点数据设置永不过期，加锁
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonWithLocal() {
        //this 对象是service,在springboot中所有的组件都是单例的，可以锁住 锁的是对象实例
        synchronized (this) {
            //缓存没有查询数据库,返回数据
            if (stringRedisTemplate.opsForValue().get("catalogJson") == null) {
                System.out.println("查询了数据库");
                Map<String, List<Catelog2Vo>> parentCid = getStringListMap();
                stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(parentCid), 1, TimeUnit.DAYS);
                return parentCid;
            }
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            System.out.println("缓存命中了");
            return result;

        }


    }

    private Map<String, List<Catelog2Vo>> getStringListMap() {
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.list();
        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));
        return parentCid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
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
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
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