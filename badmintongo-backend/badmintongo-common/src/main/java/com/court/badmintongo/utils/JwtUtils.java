package com.court.badmintongo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    /**
     * [密鑰設定]
     * 這是加密的靈魂。這串字串必須至少 32 個字元。
     * 實務上建議從 application-local.yml 讀取，這裡先幫你寫死方便測試。
     */
    private static final String SECRET_KEY = "BadmintonGo_Lawrence_Secret_Key_For_2026_Project";

    /**
     * [過期時間]
     * 設定為 24 小時 (單位：毫秒)。
     */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    // 將字串轉為符合加密演算法所需的 Key 物件
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 1. 【產生護照】 - 給 auth-service 登入成功時呼叫
     * 作用：把使用者的 ID 和 角色 封裝成一串加密字串。
     * @param authentication   身分標記
     */
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        // 從 authentication 拿到使用者名稱
        String username = authentication.getName();

        // authentication 拿到所有權限並轉為字串
        // 因為 admin 現在有兩個身分 (ADMIN, USER)
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("role", roles)              // 存入角色，例如 "ROLE_ADMIN,ROLE_MEMBER"
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 2. 【驗證並拆解護照】 - 給所有 Service 的 Filter 檢查時呼叫
     * 作用：檢查這張護照是不是真的、有沒有過期。
     * @param token 前端傳來的 Authorization Header 內容
     * @return 裡面的資料 (Claims)
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)          // 使用同樣的密鑰解密
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT 驗證失敗: {}", e.getMessage());
            // 如果驗證失敗（過期、假造），直接拋出異常，警衛會攔截
            throw new RuntimeException("憑證無效或已過期");
        }
    }

    /**
     * 3. 【計算剩餘壽命】 - 給登出 (Logout) 加入 Redis 黑名單用
     * 作用：算出這張護照還剩幾秒鐘過期，讓 Redis 幫我們自動清除黑名單。
     */
    public long getExpirationTimeLeft(String token) {
        try {
            Claims claims = parseToken(token);
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return expirationTime - currentTime;
        } catch (Exception e) {
            return 0L;
        }
    }
}
