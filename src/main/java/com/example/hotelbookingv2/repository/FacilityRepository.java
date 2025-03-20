package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, String> {
    // Можно добавить дополнительные методы поиска по нужным полям, пока не требуется
}
