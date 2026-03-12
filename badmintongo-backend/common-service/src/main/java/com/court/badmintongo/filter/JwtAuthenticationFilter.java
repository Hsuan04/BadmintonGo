package com.court.badmintongo.filter;

import com.court.badmintongo.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 從請求 Header 中取出 Authorization 欄位
        String header = request.getHeader("Authorization");

        // 2. 檢查格式是否為 "Bearer {Token}"
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7); // 裁切掉 "Bearer " 拿到純 Token

            try {
                // 3. 呼叫 JwtUtils 解析護照內容
                Claims claims = jwtUtils.parseToken(token);
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                // 4. 如果解析成功，建立 Spring Security 認可的身分證 (Authentication)
                // 注意：Spring 的角色慣例會補上 "ROLE_" 前綴
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // 5. 將身分證貼在這次請求的 Context 牆上
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // 護照造假、過期或毀損，清空身分資訊
                log.error("JWT 驗證失敗: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // 6. 繼續往下一關走 (可能是下一個 Filter 或直接進 Controller)
        filterChain.doFilter(request, response);
    }
}
