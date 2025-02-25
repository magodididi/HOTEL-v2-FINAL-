package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.HotelEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    List<HotelEntity> findByCityAndCategory(String city, String category);

    List<HotelEntity> findByCity(String city);

    List<HotelEntity> findByCategory(String category);

}
