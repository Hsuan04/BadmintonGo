package com.court.badmintongo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    /**
     * 定義 RedisTemplate，統一使用 JSON 序列化
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 序列化：將 HashValue 也改為 String 序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer()); // 關鍵改動

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 設定 Redis 訊息監聽容器 (為了 Keyspace Notifications)
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        // 自動開啟 Redis 的過期事件通知 (CONFIG SET notify-keyspace-events Ex)
        try {
            factory.getConnection().serverCommands().setConfig("notify-keyspace-events", "Ex");
        } catch (Exception e) {
            // 部分雲端環境(如 AWS)不允許程式修改配置，需手動設定，此處抓掉例外避免啟動失敗
        }
        return container;
    }
}
