package com.losca.gulimall.product.web;

import com.losca.gulimall.product.entity.CategoryEntity;
import com.losca.gulimall.product.service.CategoryService;
import com.losca.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient client;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "/index.html"})
    public String index(ModelMap map) {
        List<CategoryEntity> categories = categoryService.getLevel1Categories();
        map.addAttribute("categories", categories);
        return "index";
    }

    //index/json/catalog.json
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;

    }

    /**
     * 分布式锁redisson最简单的锁
     *
     * @return
     */
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        RLock lock = client.getLock("my-lock");
        //加锁 阻塞式等待，默认加的锁都是30s
        //lock.lock();
        lock.lock(10, TimeUnit.SECONDS);//10秒自动解锁，自动解锁时间一定要大于业务执行时间
        //阻塞式等待。默认加的锁都是30s
        //1）、锁的自动续期，如果业务超长，运行期间自动锁上新的30s。不用担心业务时间长，锁自动过期被删掉
        //2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题
        // myLock.lock(10,TimeUnit.SECONDS);   //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
        //问题：在锁时间到了以后，不会自动续期
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们制定的时间
        //2、如果我们未指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
        //只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒
        // internalLockLeaseTime 【看门狗时间】 / 3， 10s
        //最佳实战 lock.lock(10, TimeUnit.SECONDS);指定释放锁的时间 防止重新续期 手动解锁
        try {
            System.out.println("加锁成功，执行业务。。。。。。。" + Thread.currentThread().getId());
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            //解锁
            System.out.println("释放锁。。。。。。" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 保证一定能读到最新数据，修改期间，写锁是一个排它锁（互斥锁、独享锁），读锁是一个共享锁
     * 写锁没释放读锁必须等待
     * 读 + 读 ：相当于无锁，并发读，只会在Redis中记录好，所有当前的读锁。他们都会同时加锁成功
     * 写 + 读 ：必须等待写锁释放
     * 写 + 写 ：阻塞方式
     * 读 + 写 ：有读锁。写也需要等待
     * 只要有读或者写的存都必须等待
     *
     * @return
     */
    @GetMapping("/read")
    @ResponseBody
    public String read() {
        RReadWriteLock readWriteLock = client.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = readWriteLock.readLock();
        String s = null;
        try {
            rLock.lock();
            s = stringRedisTemplate.opsForValue().get("writeValue");
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping("/write")
    @ResponseBody
    public String write() {
        String s = "";
        RReadWriteLock readWriteLock = client.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        try {
            rLock.lock();
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeValue", s);
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 车库停车
     * 信号量也可以做分布式限流
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = client.getSemaphore("park");
        //获取一个信号 该park对应的值减1，减到0后会进行阻塞，知道park>0后才会进行执行
        //park.acquire();
        boolean flag = park.tryAcquire();
        return "ok=>" + flag;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = client.getSemaphore("park");
        //释放一个车位 该park对应的值加1
        park.release();
        return "ok";
    }

    /**
     * 放假、锁门
     * 1班没人了
     * 5个班，全部走完，我们才可以锁大门
     * 分布式闭锁
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = client.getCountDownLatch("door");
        door.trySetCount(5);//设置计数
        //等待闭锁完成
        door.await();
        return "放假了。。。";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = client.getCountDownLatch("door");
        door.countDown();//计数减1
        return id + "班的人都走了。。。";
    }
}
