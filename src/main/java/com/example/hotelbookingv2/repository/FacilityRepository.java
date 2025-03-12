package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<FacilityEntity, String> {
    // Можно добавить дополнительные методы поиска по нужным полям, пока не требуется
}
