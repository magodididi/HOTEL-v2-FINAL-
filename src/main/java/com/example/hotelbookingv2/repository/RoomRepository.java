package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    //SELECT r FROM Room r WHERE r.hotel.id = :hotelId
    @Query(value = "SELECT * FROM rooms r WHERE r.hotel_id = :hotelId", nativeQuery = true)
    List<Room> findRoomsByHotel(@Param("hotelId") String hotelId);


    //SELECT r.id, r.room_number, r.type, r.price, r.hotel_id FROM rooms r
    //JOIN room_facilities rf ON r.id = rf.room_id
    //JOIN facilities f ON rf.facility_id = f.id
    //WHERE f.name = :facilityName
    //nativeQuery = true
    @Query("SELECT r FROM Room r JOIN r.facilities f WHERE f.name = :facilityName")
    List<Room> findRoomsByFacility(@Param("facilityName") String facilityName);

    boolean existsByRoomNumberAndHotelId(String roomNumber, String hotelId);




}

