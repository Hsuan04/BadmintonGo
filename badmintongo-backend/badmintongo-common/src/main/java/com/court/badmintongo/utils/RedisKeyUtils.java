package com.court.badmintongo.utils;

public final class RedisKeyUtils {

    private static final String BASE_KEY = "badmintongo";
    private static final String SESSION_BASE = BASE_KEY + ":session:%s";

    /**
     * 場次 Meta 資訊 (Hash)
     * 格式: badmintongo:session:{sessionId}:meta
     */
    public static String getSessionMetaKey(String sessionId) {
        return String.format(SESSION_BASE + ":meta", sessionId);
    }

    /**
     * 場次報名池 (ZSet)
     * 格式: badmintongo:session:{sessionId}:pool
     */
    public static String getSessionPoolKey(String sessionId) {
        return String.format(SESSION_BASE + ":pool", sessionId);
    }

    /**
     * 3. 影子 Key (String) -> 用於監聽過期事件
     * 格式: badmintongo:session:{sessionId}:shadow
     */
    public static String getSessionShadowKey(String sessionId) {
        return String.format(SESSION_BASE + ":shadow", sessionId);
    }

    /**
     * 使用者報名狀態或鎖定 (String/Hash)
     * 格式: badmintongo:user:{userId}:status
     */
    public static String getUserStatusKey(String userId) {
        return String.format(BASE_KEY + ":user:%s:status", userId);
    }

    private RedisKeyUtils() {}
}
