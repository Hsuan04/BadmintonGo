package com.court.badmintongo.listener;

import com.court.badmintongo.repository.SessionRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final SessionRepository sessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer,
                                      SessionRepository sessionRepository,
                                      RedisTemplate<String, Object> redisTemplate) {
        super(listenerContainer);
        this.sessionRepository = sessionRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        System.out.println("[Redis 測試] 偵測到 Key 過期: " + expiredKey);

        // 檢查是否為我們設定的 Meta Key
        if (expiredKey.startsWith("badmintongo:session:") && expiredKey.endsWith(":meta")) {
            // 1. 解析 SessionId (假設格式為 badmintongo:session:{id}:meta)
            String[] parts = expiredKey.split(":");
            String sessionId = parts[2];
            String shadowKey = "badmintongo:session:" + sessionId + ":shadow";

            System.out.println("[Redis 測試] 準備從 Shadow 取回數據，ID: " + sessionId);

            // 2. 從 Shadow Key 撈出數據
            Map<Object, Object> shadowData = redisTemplate.opsForHash().entries(shadowKey);

            if (!shadowData.isEmpty()) {
                Integer current = Integer.parseInt((String) shadowData.get("currentParticipants"));
                String createdAt = (String) shadowData.get("createdAt");

                System.out.println("[Redis 測試] 最終人數: " + current + ", 建立時間: " + createdAt);

                // 3. 更新資料庫 (這邊假設你的 Repository 有寫好更新方法)
                sessionRepository.findById(sessionId).ifPresent(po -> {
                    po.setCurrentParticipants(current);
                    po.setUpdatedAt(LocalDateTime.now());
                    sessionRepository.save(po);
                    System.out.println("[Redis 測試] 資料庫更新成功！");
                });

                // 4. 刪除 Shadow Key (功成身退)
                redisTemplate.delete(shadowKey);
                System.out.println(" [Redis 測試] Shadow Key 已清理。");
            } else {
                System.out.println("[Redis 測試] 找不到 Shadow 數據，可能已被手動刪除。");
            }
        }
    }
}
