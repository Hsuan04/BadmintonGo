package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.RolePo;
import com.court.badmintongo.bean.po.UserInfoPo;
import com.court.badmintongo.repository.RoleRepository;
import com.court.badmintongo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(String email, String rawPassword) {
        // 1. 檢查是否已存在
        if (userInfoRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 2. 加密密碼
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 3. 建立 PO
        UserInfoPo user = new UserInfoPo();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        // 4. 設定預設角色 (假設註冊預設為 ROLE_USER)
        RolePo defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(defaultRole);

        userInfoRepository.save(user);
    }


    public static void main(String[] args) {
        // 1. 初始化 Spring Security 的加密工具
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 2. 設定你想要存入資料庫的明文密碼
        String rawPassword = "test"; // 👈 改成你想設定的密碼

        // 3. 執行加密
        String encodedPassword = encoder.encode(rawPassword);

        // 4. 印出結果
        System.out.println("========================================");
        System.out.println("明文密碼: " + rawPassword);
        System.out.println("加密後的雜湊值 (請複製下面這一串存入資料庫):");
        System.out.println(encodedPassword);
        System.out.println("========================================");
    }
}
