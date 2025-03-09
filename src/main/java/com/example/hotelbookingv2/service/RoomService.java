package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.RoomEntity;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<RoomEntity> getRoomsByHotel(String hotelId);

    Optional<RoomEntity> getRoomById(String id);

    RoomEntity saveRoom(RoomEntity room);

    void deleteRoom(String id);

    RoomEntity updateRoom(String id, RoomEntity updatedRoom);

    List<RoomEntity> getAllRooms();
}
