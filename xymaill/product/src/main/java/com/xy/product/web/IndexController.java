package com.xy.product.web;

import com.alibaba.nacos.common.util.UuidUtils;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.service.CategoryService;
import com.xy.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private  CategoryService categoryService;
//    @Autowired
//    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        //会按照配置 返回 会找classpath:/templates/下的index.html
        List<CategoryEntity> categoryEntityList = categoryService.getCatLevel1();
        model.addAttribute("categoryEntitys",categoryEntityList);
        return "index";
    }

    @GetMapping("/index/json/catalog")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //可重入锁
       //RLock rLock =  redissonClient.getLock("xy_lock");//（非公平锁 公平锁是：redisson.getFairLock("xy_lock")）
        //rLock.lock(); //阻塞等待  这个锁会自动续期（如果业务超长 锁会自动增加过期时间） 不用担心业务时间长，会将锁清除 //加锁的业务只要完成  就不会给锁续期  即使不手动解锁  30s后锁也会自动删除
        try{
           //会往redis中存入xy_lock：uuid+当前线程号
           //rLock.lock(10, TimeUnit.SECONDS); //这个方法需要特别注意  如果 业务的处理时间大于十秒 锁会自动删除 下一个线程即可抢占锁 所以一定要锁的过期时间大于业务的处理时间才行（这个锁不会自动续期）
           //特别注意： 如果我们给定了一个锁的过期时间 lock方法 就会将这个过期时间给redis脚本 也不会自动续期
           //如果我们未给过期时间，lock方法会把默认配置的看门狗时间给redis 默认事件是30s 然后 一直判断  如果业务未执行完  会一直续期（续期的规则是默认的看门狗时间/3） 也就是到了20s 会自动续期（每隔10s 会自动续期（续成看门狗的满时间））

           //最佳实战：
           //rLock.lock(30, TimeUnit.SECONDS); 推荐使用它 一般业务不会超过30s 所以 给30s即可 会省掉自动续期时间 然后业务完成  也需要手动解锁

           System.out.println("加锁成功");
       }catch(Exception e){
       } finally {
           //假设解锁代码没有运行  会不会出现死锁
           //rLock.unlock();
       }
        return "hello";
    }


    //读写锁的关键就是 读操作或者get数据的时候 加读锁 写操作 或者修改数据的时候加写锁 能保证 读到的肯定是最新的数据 写锁是互斥的 只能有一个在写 读锁只要等写都完事后 可以随便读
    //读+读 无锁 都会访问到
    //读+写 会等读锁释放  写锁才会继续写
    //写加写 互斥  阻塞 只有一个可以正常写
    //写+读 读会等写锁释放
    //总结  只要有写锁的存在 就需要等待  如果写在前 读需要等待 都在前 写需要等待

    @ResponseBody
    @GetMapping("/write")
    public String write(){
       // RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw_lock"); //获取读写锁
        String s = "";
        //RLock rLock = rReadWriteLock.writeLock();  //得到写锁
        //rLock.lock(); //给写锁枷锁
        try {
            s= UuidUtils.generateUuid();
            stringRedisTemplate.opsForValue().set("uuid",s);
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //rLock.unlock();  //手动释放锁
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String read(){
        //RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw_lock");
        //RLock rLock = rReadWriteLock.readLock();
        //rLock.lock();
        try{
            return stringRedisTemplate.opsForValue().get("uuid");
        }finally {
            //rLock.unlock();
        }
    }

    //车库停车（3个车位 需要提前给redis赋值 3） getSemaphore意思就是 有三个信号 如果抢成功acquire() 信号减1 直到减为0 就会阻塞  除非其他线程release()了 信号会自动加1
    //信号量主要做限流 成功 执行业务 不成功 就让他排队等待
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
       //RSemaphore rSemaphore =  redissonClient.getSemaphore("num");   //只要获取同一个redis key 就表明 共有信号量
        //rSemaphore.trySetPermits(3);
        //rSemaphore.acquire();
        //Boolean b = rSemaphore.tryAcquire(); //不会阻塞等待 如果 失败 直接返回false
        return "停车成功 车位-1";
    }

    @ResponseBody
    @GetMapping("/go")
    public String go(){
        //RSemaphore rSemaphore =  redissonClient.getSemaphore("num");
        //rSemaphore.release();
        return "开车成功 车位+1";
    }


    /**
     * 放假  锁门 一共5个班
     * 1 没人 锁门  2，没人 锁门  等
     */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        //RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch("door");
        //rCountDownLatch.trySetCount(5); //需要等待多少都完成 直到countDown完成了5个 就会返回成功 会把redis 中的door 设置为5
        //rCountDownLatch.await(); //等待rCountDownLatch都完成
        return "lockDoor succ";
    }


    @ResponseBody
    @GetMapping("/go/{id}")
    public String go(@PathVariable("id") Long id){
        //RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch("door");
        //rCountDownLatch.countDown(); //计数减一
        return "lockDoor succ";
    }
}
