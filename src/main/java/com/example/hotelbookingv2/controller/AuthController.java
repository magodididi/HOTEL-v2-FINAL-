package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.model.User;
import com.example.hotelbookingv2.service.AuthService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String passwordRepeat;
        private String email;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * Регистрация пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getPasswordRepeat(),
                request.getEmail()
        );
        return ResponseEntity.ok(user);
    }

    /**
     * Логин обычного пользователя
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(user);
    }

    /**
     * Логин сотрудника или администратора
     */
    @PostMapping("/login-employee")
    public ResponseEntity<User> loginEmployee(@RequestBody LoginRequest request) {
        User user = authService.loginEmployee(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(user);
    }
}
