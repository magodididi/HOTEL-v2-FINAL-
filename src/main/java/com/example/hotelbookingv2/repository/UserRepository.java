package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // Добавляем метод для подсчета пользователей по роли
    @Query("SELECT COUNT(u) FROM User u WHERE :role MEMBER OF u.roles")
    Long countByRolesContaining(@Param("role") String role);
}
