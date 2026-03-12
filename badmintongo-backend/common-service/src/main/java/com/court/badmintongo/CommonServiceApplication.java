package com.court.badmintongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CommonServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonServiceApplication.class, args);
    }
}
