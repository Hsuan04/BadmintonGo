package com.court.badmintongo.controller;

import com.court.badmintongo.bean.LoginRq;
import com.court.badmintongo.constant.SystemEnum.UserRole;
import com.court.badmintongo.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

//    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestBody LoginRq loginRq) {

        // 1. AuthenticationManager 認證(呼叫 UserDetailsService 並比對 BCrypt)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword())
        );

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(UserRole.ADMIN.getCode()));

        if (!isAdmin) {
            throw new AccessDeniedException("wrong account / email or password");
        }

        // 2. 認證成功，將結果存入 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 透過工具類生成真正的 JWT Token
        String token = jwtUtils.generateToken(authentication);

        // 4. 取得使用者的 Role 列表
        String primaryRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        // 5. 組裝回傳結果
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("role", primaryRole.replace("ROLE_", "")); // 轉成前端好用的格式，如 ADMIN

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {


        // 1. 取得當前使用者資訊 (供 AOP 或日誌使用)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 2. 執行清除 Context 的動作 (這是 LogoutFilter 原本幫你做的事)
        new SecurityContextLogoutHandler().logout(request, response, auth);

        // 3. 回傳結果 (會被 AOP 攔截)
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Admin logout successful");
        return ResponseEntity.ok(res);
    }

}
