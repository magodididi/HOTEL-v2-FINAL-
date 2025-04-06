package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.Hotel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {
    List<Hotel> findByCityAndCategory(String city, String category);

    List<Hotel> findByCity(String city);

    List<Hotel> findByCategory(String category);

    Optional<Hotel> getHotelById(String id);
}
