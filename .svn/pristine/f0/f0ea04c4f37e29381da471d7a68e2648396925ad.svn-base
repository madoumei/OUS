package com.utils.empUtils;

import com.utils.SysLog;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bryan
 * @version 1.0
 * @description:
 * @date 2021/7/12 上午11:11
 */
public class EmpUtils {
    /**
     * 员工同步信息存储
     *
     * @param redisTemplate
     * @param key
     * @param hashKeyDate
     * @param hashKeyNum
     * @param date
     * @param number
     */
    public static void addEmpSyncMsg(RedisTemplate redisTemplate, String key, String hashKeyDate, String hashKeyNum, LocalDateTime date, Integer number) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hashOperations.put(key, hashKeyDate, date.format(dateTimeFormatter));
        hashOperations.put(key, hashKeyNum, number);
        SysLog.info("同步员工成功："+hashOperations);
    }
}
