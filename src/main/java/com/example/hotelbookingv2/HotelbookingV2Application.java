package com.example.hotelbookingv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
@EntityScan("com.example.hotelbookingv2.model")
@EnableJpaRepositories("com.example.hotelbookingv2.repository")
public class HotelbookingV2Application {
    public static void main(String[] args) {
        SpringApplication.run(HotelbookingV2Application.class, args);
    }
}

