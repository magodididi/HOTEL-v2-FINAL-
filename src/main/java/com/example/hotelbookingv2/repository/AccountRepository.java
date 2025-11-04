package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.Account;
import com.example.hotelbookingv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);
    boolean existsByUser(User user);
}
