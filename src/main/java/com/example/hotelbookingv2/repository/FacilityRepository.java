package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, Long> {
}
