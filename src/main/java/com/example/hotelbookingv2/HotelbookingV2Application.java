package com.example.hotelbookingv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.hotelbookingv2.model")
@EnableJpaRepositories("com.example.hotelbookingv2.repository")  // Добавляем JPA-репозитории
public class HotelbookingV2Application {
    public static void main(String[] args) {
        SpringApplication.run(HotelbookingV2Application.class, args);
    }
}

