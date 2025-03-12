package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.FacilityEntity;
import com.example.hotelbookingv2.model.RoomEntity;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.service.RoomService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;

    public RoomServiceImpl(RoomRepository roomRepository, FacilityRepository facilityRepository) {
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<RoomEntity> getAllRooms() {
        List<RoomEntity> rooms = roomRepository.findAll();
        rooms.forEach(room -> room.getFacilities()
                .forEach(facility -> {}));
        return rooms;
    }

    @Override
    public List<RoomEntity> getRoomsByHotel(String hotelId) {
        List<RoomEntity> rooms = roomRepository.findByHotelId(hotelId);
        rooms.forEach(room -> room.getFacilities()
                .forEach(facility -> {}));
        return rooms;
    }

    @Override
    public Optional<RoomEntity> getRoomById(String id) {
        Optional<RoomEntity> roomOpt = roomRepository.findById(id);
        roomOpt.ifPresent(room -> room.getFacilities()
                .forEach(facility -> {}));
        return roomOpt;
    }

    @Override
    @Transactional
    public RoomEntity saveRoom(RoomEntity room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoomEntity updateRoom(String id, RoomEntity updatedRoom) {
        return roomRepository.findById(id).map(room -> {
            room.setRoomNumber(updatedRoom.getRoomNumber());
            room.setType(updatedRoom.getType());
            room.setPrice(updatedRoom.getPrice());

            if (updatedRoom.getFacilities() != null) {
                for (FacilityEntity facility : updatedRoom.getFacilities()) {
                    FacilityEntity existingFacility = facilityRepository.findById(facility.getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Удобство не найдено: " + facility.getId()
                            ));
                    if (!room.getFacilities().contains(existingFacility)) {
                        room.addFacility(existingFacility);
                    }
                }
            }

            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException(
                "Room not found with id " + id)
        );
    }
}


