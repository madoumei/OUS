package com.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author bryan
 * @version 1.0
 * @description:
 * @date 2021/7/22 下午4:44
 */
@Configuration
public class RedisConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        // 可以添加多个 messageListener，配置不同的交换机
        container.addMessageListener(listenerAdapter, new PatternTopic("*"));
        return container;
    }

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     *
     * @param myRedisKeyExpiredMessageDelegate
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(MyRedisKeyExpiredMessageDelegate myRedisKeyExpiredMessageDelegate) {
        return new MessageListenerAdapter(myRedisKeyExpiredMessageDelegate, "handleMessage");
    }
}
