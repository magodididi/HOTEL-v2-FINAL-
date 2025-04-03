package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.Room;
import java.util.List;

public interface RoomService {

    List<Room> findRoomsByHotel(String hotelId);

    List<Room> findRoomsByFacility(String facilityName);

    List<Room> getRoomsByHotel(String hotelId);

    Room saveRoom(Room room);

    void deleteRoom(String id);

    Room updateRoom(String id, Room updatedRoom);

    Room getRoomById(String id);  // Было Optional<Room>



}
