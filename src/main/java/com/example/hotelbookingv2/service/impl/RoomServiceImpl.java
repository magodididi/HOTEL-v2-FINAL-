package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.service.RoomService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;
    private final RoomCache roomCache;

    @Autowired
    public RoomServiceImpl(
            RoomRepository roomRepository,
            FacilityRepository facilityRepository,
            RoomCache roomCache
    ) {
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
        this.roomCache = roomCache;
    }

    @Override
    public List<Room> findRoomsByHotel(String hotelId) {
        return roomRepository.findRoomsByHotel(hotelId);
    }

    @Override
    public List<Room> findRoomsByFacility(String facilityName) {
        return roomRepository.findRoomsByFacility(facilityName);
    }

    @Override
    public List<Room> getRoomsByHotel(String hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        rooms.forEach(room -> room.getFacilities().forEach(facility -> {
        }));
        return rooms;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        rooms.forEach(room -> room.getFacilities().forEach(facility -> {
        }));
        return rooms;
    }

    @Override
    public Optional<Room> getRoomById(String id) {

        Room cachedRoom = roomCache.get(id);
        if (cachedRoom != null) {
            return Optional.of(cachedRoom);
        }

        Optional<Room> roomOpt = roomRepository.findById(id);
        roomOpt.ifPresent(room -> {
            room.getFacilities().forEach(facility -> {
            });
            roomCache.put(room.getId(), room);
        });

        return roomOpt;
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        Room savedRoom = roomRepository.save(room);
        roomCache.put(savedRoom.getId(), savedRoom);
        return savedRoom;
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
        roomCache.remove(id);
    }

    @Override
    @Transactional
    public Room updateRoom(String id, Room updatedRoom) {
        return roomRepository.findById(id).map(room -> {
            room.setRoomNumber(updatedRoom.getRoomNumber());
            room.setType(updatedRoom.getType());
            room.setPrice(updatedRoom.getPrice());

            if (updatedRoom.getFacilities() != null) {
                List<String> facilityIds = updatedRoom.getFacilities().stream()
                        .map(Facility::getId)
                        .toList();
                List<Facility> facilities = facilityRepository.findAllById(facilityIds);
                room.setFacilities(new ArrayList<>(facilities));
            }

            Room updated = roomRepository.save(room);

            roomCache.put(updated.getId(), updated);

            return updated;
        }).orElseThrow(() -> new RuntimeException("Room not found with id " + id));
    }
}
