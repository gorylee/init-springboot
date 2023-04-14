package com.openAi.yunchat.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    private static final RedisUtils redisUtils = new RedisUtils();

    @Value("${redis-config.default-lock-expire-time:60}")
    private Long LOCK_EXPIRE;//默认过期时间

    @Autowired
    private RedisTemplate redisTemplate;


    @PostConstruct
    public void initialize() {
        redisUtils.redisTemplate = redisTemplate;
        redisUtils.LOCK_EXPIRE = LOCK_EXPIRE;
    }

    /**
     * 分布式锁  不存在返回true
     * @param key
     * @param expireTime
     * @return
     */
    public static boolean lock(String key,Long expireTime){
        if(expireTime==null) expireTime = redisUtils.LOCK_EXPIRE;
        return redisUtils.redisTemplate.opsForValue().setIfAbsent(key, 1, expireTime, TimeUnit.SECONDS);
    }
    /**
     * 释放key
     * @param key
     */
    public static void delete(String key){
        redisUtils.redisTemplate.delete(key);
    }

    /**
     * 批量释放key
     * @param keys
     */
    public static void delete(List<String> keys){
        redisUtils.redisTemplate.delete(keys);
    }

    /**
     * 普通缓存设置
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public static boolean set(String key, Object value) {
        try {
            redisUtils.redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisUtils.redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisUtils.redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入并设置过期时间
     * @param key   键
     * @param value 值
     * @param expireTime  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return 是否成功
     */
    public static boolean set(String key, Object value, int expireTime) {
        try {
            if (expireTime > 0) {
                redisUtils.redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
                return true;
            } else {
                return set(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(String key, long delta) {
        return redisUtils.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(String key, long delta) {
        return redisUtils.redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * set缓存放入并设置过期时间
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public static boolean setList(String key, Object value, long expiredTime){
        try {
            redisUtils.redisTemplate.opsForList().leftPush(key,value);
            //设置过期时间
            redisUtils.redisTemplate.expire(key,expiredTime,TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取set集合
     * @param key
     * @return
     */
    public static List<Object> getList(String key){
        return redisUtils.redisTemplate.opsForList().range(key, 0, -1);
    }


}
