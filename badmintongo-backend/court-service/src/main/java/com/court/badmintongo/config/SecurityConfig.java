package com.court.badmintongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()      // 1. Swagger
                        .requestMatchers("/api/court/**").permitAll()
                        .requestMatchers("/api/common/**").permitAll()
//                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/courts/**").permitAll()        // 2. 球場查詢 (GET)：所有人都可以
//                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/courts/**").hasRole("ADMIN")  // 3. 球場的新增 (POST):更新 (PUT)、刪除 (DELETE)：只有 ADMIN 可以
//                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/courts/**").hasRole("ADMIN")   // 4. 球場的新更新 (PUT):ADMIN 可以
//                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/courts/**").hasRole("ADMIN")// 5. 球場的刪除 (DELETE): ADMIN 可以
                        .anyRequest().authenticated());  // 其他都需要登入
        return http.build();
    }

    // 💡 關鍵 3：定義跨域規則 (這能確保 Preflight OPTIONS 請求順利通過)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
