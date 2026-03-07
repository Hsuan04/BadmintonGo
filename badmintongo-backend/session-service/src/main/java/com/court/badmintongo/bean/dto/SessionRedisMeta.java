package com.court.badmintongo.bean.dto;

import java.util.Map;

/**
 * 存放於 Redis 中的即時場次資訊
 */
public record SessionRedisMeta(
        Integer maxParticipants,
        Integer currentParticipants,
        Integer status,
        String createdAt
) {
    public static SessionRedisMeta fromMap(Map<Object, Object> map) {
        return new SessionRedisMeta(
                Integer.parseInt((String) map.get("maxParticipants")),
                Integer.parseInt((String) map.get("currentParticipants")),
                Integer.parseInt((String) map.get("status")),
                (String) map.get("createdAt")
        );
    }
}
