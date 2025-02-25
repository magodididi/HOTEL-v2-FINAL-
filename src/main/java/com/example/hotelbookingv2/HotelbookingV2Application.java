package com.example.hotelbookingv2;

import com.example.hotelbookingv2.service.DatabaseInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelbookingV2Application implements CommandLineRunner {

    private final DatabaseInitializerService databaseInitializerService;

    public HotelbookingV2Application(DatabaseInitializerService databaseInitializerService) {
        this.databaseInitializerService = databaseInitializerService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HotelbookingV2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseInitializerService.clearDatabase();
        databaseInitializerService.initializeDatabase();
    }
}