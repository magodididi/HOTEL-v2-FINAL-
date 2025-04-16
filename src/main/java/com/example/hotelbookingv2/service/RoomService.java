package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.exception.AlreadyExistsException;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;
    private final RoomCache roomCache;

    @Autowired
    public RoomService(RoomRepository roomRepository, FacilityRepository facilityRepository,
                           RoomCache roomCache) {
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
        this.roomCache = roomCache;
    }

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

    public Room getRoomById(String id) {
        Room room = roomCache.get(id);
        if (room == null) {
            room = roomRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            roomCache.put(id, room);  // Здесь обновляется кеш
        }
        return room;
    }


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
            throw new InvalidInputException("Комната с номером " + room.getRoomNumber()
                    + " уже существует в этом отеле.");
        }

        Room savedRoom = roomRepository.save(room);
        roomCache.put(savedRoom.getId(), savedRoom);
        return savedRoom;
    }


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

    @Transactional
    public List<Room> saveRoomsBulk(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            throw new InvalidInputException("Список комнат не может быть пустым.");
        }

        List<String> duplicateRoomNumbers = rooms.stream()
                .collect(Collectors.groupingBy(Room::getRoomNumber, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateRoomNumbers.isEmpty()) {
            throw new InvalidInputException("В запросе дублируются номера комнат: "
                    + duplicateRoomNumbers);
        }

        List<String> existingRoomNumbers = rooms.stream()
                .filter(room -> room.getHotel() != null && room.getRoomNumber() != null)
                .filter(room -> roomRepository.existsByRoomNumberAndHotelId(room.getRoomNumber(),
                        room.getHotel().getId()))
                .map(Room::getRoomNumber)
                .toList();

        if (!existingRoomNumbers.isEmpty()) {
            throw new AlreadyExistsException("Комнаты с номерами " + existingRoomNumbers
                    + " уже существуют в отеле.");
        }

        List<String> invalidRooms = rooms.stream()
                .filter(room -> room.getRoomNumber() == null || room.getRoomNumber().isBlank()
                        || room.getType() == null || room.getPrice() == null
                        || room.getPrice() < 0.1 || room.getHotel() == null)
                .map(Room::getRoomNumber)
                .toList();

        if (!invalidRooms.isEmpty()) {
            throw new InvalidInputException("Недопустимые данные у комнат с номерами: "
                    + invalidRooms);
        }

        List<Room> savedRooms = roomRepository.saveAll(rooms);
        savedRooms.forEach(room -> roomCache.put(room.getId(), room));

        return savedRooms;
    }
}

