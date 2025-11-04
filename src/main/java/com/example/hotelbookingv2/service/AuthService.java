package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.Account;
import com.example.hotelbookingv2.model.User;
import com.example.hotelbookingv2.repository.AccountRepository;
import com.example.hotelbookingv2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository; // ✅ добавляем
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Регистрация обычного пользователя с автоматическим созданием аккаунта
    public User register(String username, String password, String passwordRepeat, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (!password.equals(passwordRepeat)) {
            throw new RuntimeException("Passwords do not match");
        }

        // Создаём пользователя
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(Set.of("ROLE_USER"));
        userRepository.save(user);

        // 👇 Автоматически создаём пустой профиль Account
        Account account = new Account();
        account.setUser(user);
        accountRepository.save(account);

        return user;
    }

    // Вход пользователя
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    // Вход сотрудника или админа
    public User loginEmployee(String username, String password) {
        User user = login(username, password);

        if (!user.getRoles().contains("ROLE_ADMIN") && !user.getRoles().contains("ROLE_EMPLOYEE")) {
            throw new RuntimeException("Access denied: not an employee or admin");
        }

        return user;
    }
}
