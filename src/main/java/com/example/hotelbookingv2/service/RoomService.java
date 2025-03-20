package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    List<Room> findRoomsByHotel(String hotelId);

    List<Room> findRoomsByFacility(String facilityName);

    List<Room> getRoomsByHotel(String hotelId);

    Optional<Room> getRoomById(String id);

    Room saveRoom(Room room);

    void deleteRoom(String id);

    Room updateRoom(String id, Room updatedRoom);

    List<Room> getAllRooms();  // Добавлен метод!
}
