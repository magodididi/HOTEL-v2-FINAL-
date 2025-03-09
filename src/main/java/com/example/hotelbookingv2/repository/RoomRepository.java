package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.RoomEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    List<RoomEntity> findByHotelId(String hotelId);
}
