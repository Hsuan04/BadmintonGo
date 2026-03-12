package com.court.badmintongo.config;

import com.court.badmintongo.filter.JwtAuthenticationFilter;
import com.court.badmintongo.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 1. 密碼加密器：註冊時加密、登入時比對
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 2. 認證管理員：Spring Security 的核心元件，後續在 AuthService 裡呼叫它來驗證帳密
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 3. 護照局大門守則 (Filter Chain)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 公開路徑 (permitAll)
                        .requestMatchers("/api/auth/login", "/api/auth/admin/login").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // 2. 特殊身分檢查 (Admin)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 3. 只要有登入即可 (Authenticated)
                        .requestMatchers(
                                "/api/auth/logout",
                                "/api/auth/admin/logout",
                                "/api/auth/change-password"
                        ).authenticated()

                        // 4.所有的 Request
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write("{\"success\": false, \"message\": \"身分驗證失敗，請先登入\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(403);
                            response.getWriter().write("{\"success\": false, \"message\": \"權限不足，無法存取此資源\"}");
                        })
                )
                // 刪除原本重複的 .authorizeHttpRequests 區塊
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
