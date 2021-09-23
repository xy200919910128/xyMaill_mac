package com.xy.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.common.utils.PageUtils;
import com.xy.common.utils.Query;
import com.xy.product.dao.CategoryDao;
import com.xy.product.entity.CategoryEntity;
import com.xy.product.service.CategoryBrandRelationService;
import com.xy.product.service.CategoryService;
import com.xy.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> queryTree() {
        //获取一级分类
        List<CategoryEntity> allCateList =  baseMapper.selectList(null);
        List<CategoryEntity> oneCateList = new ArrayList<>();
        if(allCateList!=null){
            oneCateList = allCateList.stream().filter((categoryEntity)->{
                return categoryEntity.getParentCid()==0;
            }).filter((categoryEntity)->{
                return categoryEntity.getShowStatus()>0;
            }).map((categoryEntity)->{
                 categoryEntity.setChildCategory(getChildCateList(categoryEntity,allCateList));
                 return categoryEntity;
            }).sorted((menu1,menu2)->{
                 return (menu1.getSort()==null?Integer.valueOf(0):menu1.getSort()).compareTo(menu2.getSort()==null?Integer.valueOf(0):menu2.getSort());
            }).collect(Collectors.toList());
        }

        return oneCateList;
    }

    @Override
    public Integer logicDelete(List<Long> ids) {
        return categoryDao.logicDelete(ids);
    }

    @Override
    public Long[] getCatPathByGroupCatId(Long catelogId) {
        List<Long> list = new ArrayList<>();
        return getCatByParCatId(catelogId,list);
    }

    @Override
    @Transactional
    //@CacheEvict(value = "category",key = "'getCatalogJson'")
    //@CacheEvict(value = "category",allEntries = true)  这个注解会将category的值category这个分区下的所有内容都会删除

    //业务规定  只要是同一个类型的数据  将value都设置成一个 可以设置为不同的 默认分区名就是前缀 配置中不指定前缀
    //如果想用@cachePut会配合@CacheEvict （同时使用） 必须保证方法要返回数据而且要和定义的key的数据类型是一致的 他会把最新更改的数据重新放入缓存中
    //常规数据 （读多写少，一致性要求不高）：都可以使用springcache  添加过期时间就可以了  省略了 大段的写法； 特殊数据 需要特殊处理（可能自己需要加分布式的读写锁）{spring cache 只在读模式加了本地锁 写模式没加锁 }
    @Caching(evict = {
            @CacheEvict(value = "category",key = "'getCatalogJson'"),
            @CacheEvict(value = "category",key = "'getCatLevel1'")
    })
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if(StringUtils.isNotEmpty(category.getName())){
            categoryBrandRelationService.updateByCatId(category.getCatId(),category.getName());
        }
    }

    @Override
    @Cacheable(value = "category",key = "'getCatLevel1'")//当前方法需要缓存 如果缓存中有 方法不需要调用 否则会执行方法 查询数据库并放入缓存  每一个数据都要指定一个名字（规定按照业务类型存放）
    //这个缓存的默认行为：1 key 是自动生成的：SimpleKey [] 2，缓存的序列化默认使用jdk序列化 3，默认的超时时间是永不过期
    //自定义：需要设置缓存的key key属性指定 （spel表达式） 如果是字符串 需要在双引号写单引号 如果是表达式 需要"#root.method.name"
    // 设置序列化（以json）
    // 设置过期时间 :在配置文件中修改ttl
    public List<CategoryEntity> getCatLevel1() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_cid","0");
        List<CategoryEntity> allCateList =  baseMapper.selectList(queryWrapper);
        return allCateList;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        selectList = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return selectList;
    }

    @Override
    @Cacheable(value = "category" ,key = "'getCatalogJson'")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map = getCatalogJsonFromDb();
        return map;
    }

    //TODO 使用新版springboot 会产生 堆外内存溢出异常：OutOfDirectMemoryError
    //因为新版的springboot 会使用lettuce作为操作redis的客户端 这是lettuce 的bug
    //解决方案：
    //升级 lettuce（目前尚未解决此bug） 2使用jedis
    private Map<String, List<Catelog2Vo>> getCatalogJsonRedisTemplate(){
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String catJson = stringStringValueOperations.get("catJson");
        if(StringUtils.isEmpty(catJson)){
//            Map<String, List<Catelog2Vo>> map = getCatalogJsonFromDbFromRedissonLock();
//            return map;
        }
        //可以将jsonStr转为任意对象的方法（反序列化）
        return JSONObject.parseObject(catJson,new TypeReference<Map<String, List<Catelog2Vo>> >(){});
    }

    //解决并发的缓存击穿 需要加锁 1，本地锁 1)将整体查询数据库的过程 ：synchronized(this) 可以  因为spring boot的对象都是单例的
    //从数据库查询并封装分类数据
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> parentCid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2VOS = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2VO = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3Catalog)) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catalog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3VO = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(collect);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        return parentCid;
    }

    //从数据库查询并封装分类数据（使用本地锁）
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbLocalLock() {

        synchronized (this){
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            return getAndSetCacheCatListMapFromDb(stringStringValueOperations);
        }
    }


    //从数据库查询并封装分类数据（使用本地锁）
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbFromfenbushiLock() {
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            String uuid = UUID.randomUUID().toString();
            //加索的时候添加 uuid 和过期时间 这个方法多个服务 只有一个会返回true
            Boolean lock = stringStringValueOperations.setIfAbsent("lock",uuid,300,TimeUnit.SECONDS);
            if(lock){
                System.out.println("获取分布式锁成功");
                Map<String, List<Catelog2Vo>> dataFromDb;
                try{
                    dataFromDb = getAndSetCacheCatListMapFromDb(stringStringValueOperations);
                }finally {
                    String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
                   Long result =  stringRedisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),Arrays.asList("lock"),uuid);
                }
                return dataFromDb;
            }else{
                System.out.println("分布式锁获取失败，等待重试");
                try{
                    Thread.sleep(200);
                }catch(Exception e){

                }
                return getCatalogJsonFromDbFromfenbushiLock();
            }
    }


    //缓存中的数据和数据库的数据如何保持一致？ // 2种：双写模式（数据库和缓存都写）和失效模式（数据库更新 缓存删除 让下次读缓存先读数据库）
    //从数据库查询并封装分类数据（使用redisson锁）
//    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbFromRedissonLock() {
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//            Map<String, List<Catelog2Vo>> dataFromDb;
//            //锁的粒度 越细 越好
//            //锁的粒度：缓存具体的某个数据：11号商品 product_11_lock 这样涉及
//            RLock rLock = redissonClient.getLock("catJsonLock");
//            rLock.lock();
//            try{
//                dataFromDb = getAndSetCacheCatListMapFromDb(stringStringValueOperations);
//            }finally {
//                rLock.unlock();
//            }
//            return  dataFromDb;
//    }


    private Map<String, List<Catelog2Vo>> getAndSetCacheCatListMapFromDb(ValueOperations<String, String> stringStringValueOperations) {
        String catJson = stringStringValueOperations.get("catJson");
        if (StringUtils.isNotEmpty(catJson)) {
            //可以将jsonStr转为任意对象的方法（反序列化）
            return JSONObject.parseObject(catJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> parentCid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2VOS = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2VO = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3Catalog)) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catalog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3VO = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(collect);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        //将对象转为jsonStr存入缓存（序列化）
        stringStringValueOperations.set("catJson", JSONObject.toJSONString(parentCid), 1, TimeUnit.DAYS);
        return parentCid;
    }

    private Long[] getCatByParCatId(Long parId,List<Long> list) {
        list.add(parId);
        CategoryEntity category = categoryDao.selectById(parId);
        if(category!=null&&category.getParentCid()!=0){
            getCatByParCatId(category.getParentCid(),list);
        }
        Collections.reverse(list);
        return list.toArray(new Long[list.size()]);
    }


    /**
     * 根据父类id查询子类类别
     */
    private List<CategoryEntity> getChildCateList (CategoryEntity categoryEntity,List<CategoryEntity> allCateList) {
        List<CategoryEntity> childlList =  allCateList.stream().filter((menu)->{
            return menu.getParentCid() == categoryEntity.getCatId();
        }).filter((menu)->{
            return menu.getShowStatus()>0;
        }).map((menu)->{
            menu.setChildCategory(getChildCateList(menu,allCateList));
            return menu;
        }).sorted((m1,m2)->{
            return (m1.getSort()==null?Integer.valueOf(0):m1.getSort()).compareTo(m2.getSort()==null?Integer.valueOf(0):m2.getSort());
        }).collect(Collectors.toList());

        return childlList;
    }

}