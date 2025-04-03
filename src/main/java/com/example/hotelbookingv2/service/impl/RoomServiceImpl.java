package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.exception.AlreadyExistsException;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.service.RoomService;
import java.util.ArrayList;
import java.util.List;
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
    public RoomServiceImpl(RoomRepository roomRepository, FacilityRepository facilityRepository,
                           RoomCache roomCache) {
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
        this.roomCache = roomCache;
    }

    @Override
    public List<Room> findRoomsByHotel(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) {
            throw new InvalidInputException("ID отеля не может быть пустым или равным null.");
        }
        List<Room> rooms = roomRepository.findRoomsByHotel(hotelId);
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("Комнаты не найдены для отеля с ID: " + hotelId);
        }
        return rooms;
    }

    @Override
    public List<Room> findRoomsByFacility(String facilityName) {
        if (facilityName == null || facilityName.isBlank()) {
            throw new InvalidInputException("Название удобства не может"
                    + " быть пустым или равным null.");
        }
        List<Room> rooms = roomRepository.findRoomsByFacility(facilityName);
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("Комнаты с удобством " + facilityName
                    + " не найдены.");
        }
        return rooms;
    }

    @Override
    public Room getRoomById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidInputException("ID комнаты не может"
                    + " быть пустым или равным null.");
        }
        Room cachedRoom = roomCache.get(id);
        if (cachedRoom != null) {
            return cachedRoom;
        }

        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комната с ID: "
                        + id + " не найден."));
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        if (room == null || room.getHotel() == null || room.getRoomNumber() == null
                || room.getType() == null || room.getPrice() == null) {
            throw new InvalidInputException("Предоставлены некорректные данные для комнаты.");
        }

        if (room.getPrice() < 0.1) {
            throw new InvalidInputException("Цена должна быть больше 0.");
        }

        if (roomRepository.existsByRoomNumberAndHotelId(room.getRoomNumber(),
                room.getHotel().getId())) {
            throw new AlreadyExistsException("Комната с номером " + room.getRoomNumber()
                    + " уже существует в этом отеле.");
        }

        Room savedRoom = roomRepository.save(room);
        roomCache.put(savedRoom.getId(), savedRoom);
        return savedRoom;
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidInputException("ID комнаты не может быть пустым или равным null.");
        }
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Комната с ID " + id + " не найдена.");
        }
        roomRepository.deleteById(id);
        roomCache.remove(id);
    }

    @Override
    @Transactional
    public Room updateRoom(String id, Room updatedRoom) {
        if (id == null || id.isBlank() || updatedRoom == null) {
            throw new InvalidInputException(
                    "ID комнаты и данные для обновления не могут быть пустыми."
            );
        }

        return roomRepository.findById(id).map(room -> {
            if (updatedRoom.getRoomNumber() == null || updatedRoom.getRoomNumber().isBlank()) {
                throw new InvalidInputException("Номер комнаты не может быть пустым.");
            }
            if (updatedRoom.getPrice() == null || updatedRoom.getPrice() < 0.1) {
                throw new InvalidInputException("Цена должна быть больше 0.");
            }

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
        }).orElseThrow(() -> new ResourceNotFoundException("Комната с ID: " + id + " не найдена."));
    }

    @Override
    public List<Room> getRoomsByHotel(String hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }
}

