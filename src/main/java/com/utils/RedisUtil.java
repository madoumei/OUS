package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @package: com.shuyu.blog.util
 * @className: RedisUtil
 * @description:
 * @author: Shuyu.Wang
 * @date: 2019-07-14 14:42
 * @since: 0.1
 **/
@Component
@Slf4j
public class RedisUtil {

    private static final Long SUCCESS = 1L;

    @Autowired
    private RedisTemplate redisTemplate;
    // =============================common============================



    /**
     * 获取锁
     * @param lockKey
     * @param value
     * @param expireTime：单位-秒
     * @return
     */
    public boolean getLock(String lockKey, Object value, int expireTime) {
        try {
            log.info("添加分布式锁key={},expireTime={}",lockKey,expireTime);
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
            RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            // 3.创建 序列化类
            GenericToStringSerializer genericToStringSerializer = new GenericToStringSerializer(Object.class);
            // 6.序列化类，对象映射设置
            // 7.设置 value 的转化格式和 key 的转化格式
            redisTemplate.setValueSerializer(genericToStringSerializer);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.afterPropertiesSet();
            Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value, expireTime);
            if (SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     */
    public boolean releaseLock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        SysLog.info("releaseLock key="+lockKey+" result="+result);
        return result == 1?true:false;
    }

    public boolean hasKey(String lockKey) {
        Object value = redisTemplate.opsForValue().get(lockKey);
        return value != null;
    }

    /**
     *
     * @param key
     * @param liveTime
     * @return
     */
    public Long incr(String key, long liveTime) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }

        return increment;
    }
}
