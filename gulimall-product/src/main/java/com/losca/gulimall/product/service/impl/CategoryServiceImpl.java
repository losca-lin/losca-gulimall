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
import com.losca.gulimall.product.service.CategoryBrandRelationService;
import com.losca.gulimall.product.service.CategoryService;
import com.losca.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public R listTree() {
        log.info("listTree进来了");
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

    /**
     * 每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区(按照业务类型分)】
     * 代表当前方法的结果需要缓存，如果缓存中有，方法都不用调用，如果缓存中没有，会调用方法。最后将方法的结果放入缓存
     * 默认行为
     *      如果缓存中有，方法不再调用
     *      key是默认生成的:缓存的名字::SimpleKey::[](自动生成key值)
     *      缓存的value值，默认使用jdk序列化机制，将序列化的数据存到redis中
     *      默认时间是 -1：
     *
     *   自定义操作：key的生成
     *      指定生成缓存的key：key属性指定，接收一个Spel
     *      指定缓存的数据的存活时间:配置文档中修改存活时间
     *      将数据保存为json格式
     *
     *
     * 4、Spring-Cache的不足之处：
     *  1）、读模式
     *      缓存穿透：查询一个null数据。解决方案：缓存空数据
     *      缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用sync = true来解决击穿问题
     *      缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间
     *  2)、写模式：（缓存与数据库一致）
     *      1）、读写加锁。
     *      2）、引入Canal,感知到MySQL的更新去更新Redis
     *      3）、读多写多，直接去数据库查询就行
     *
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用Spring-Cache）：写模式(只要缓存的数据有过期时间就足够了)
     *      特殊数据：特殊设计
     *
     *  原理：
     *      CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        log.info("getLevel1Categories进来了。。。。。。。。。");
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", "0"));
    }

    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return getStringListMap();
    }

    /**
     * 测试分布式锁 核心使用redis的setnx指令 实际生产不推荐
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonWithRedisson() {
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

    /**
     * @CacheEvict(value = "category",allEntries = true) 删除某个分区下的所有数据
     * 多个CacheEvict操作
     *  @Caching(evict = {
     *          @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
     *          @CacheEvict(value = "category",key = "'getCatalogJson'")
     * @param category
     */
    @CacheEvict(value = "category",allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
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