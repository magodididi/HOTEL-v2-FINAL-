package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.service.RoomService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;
    private final HotelRepository hotelRepository;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();

        List<RoomDto> roomDtos = rooms.stream()
                .map(room -> new RoomDto(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        room.getPrice(),
                        room.getHotel().getId(),
                        room.getFacilities().stream()
                                .map(facility -> new FacilityDto(
                                        facility.getId(),
                                        facility.getName()
                                ))
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(roomDtos);
    }



    @GetMapping("/by-hotels/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(@PathVariable String hotelId) {
        List<Room> rooms = roomService.findRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/by-facility")
    public ResponseEntity<List<Room>> getRoomsByFacility(@RequestParam String facilityName) {
        List<Room> rooms = roomService.findRoomsByFacility(facilityName);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable String id) {
        Room room = roomService.getRoomById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Комната с ID " + id + " не найдена")
                );

        RoomDto roomDto = new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPrice(),
                room.getHotel().getId(),
                room.getFacilities().stream()
                        .map(f -> new FacilityDto(f.getId(), f.getName()))
                        .toList()
        );

        return ResponseEntity.ok(roomDto);
    }

    @GetMapping("/{roomId}/facilities")
    public ResponseEntity<List<FacilityDto>> getFacilitiesByRoom(@PathVariable String roomId) {
        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Комната с ID " + roomId + " не найдена"
                ));

        return ResponseEntity.ok(
                room.getFacilities().stream()
                        .map(facility -> new FacilityDto(
                                facility.getId(),
                                facility.getName())
                        )
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomCreateDto) {
        if (roomCreateDto.getHotelId() == null || roomCreateDto.getHotelId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Hotel hotel = hotelRepository.findById(roomCreateDto.getHotelId())
                .orElseThrow(() -> new RuntimeException(
                        "Отель не найден с ID " + roomCreateDto.getHotelId()
                ));

        Room room = new Room();
        room.setRoomNumber(roomCreateDto.getRoomNumber());
        room.setType(roomCreateDto.getType());
        room.setPrice(roomCreateDto.getPrice());
        room.setHotel(hotel);
        room.setId(UUID.randomUUID().toString());

        Room createdRoom = roomService.saveRoom(room);
        RoomDto createdRoomDto = new RoomDto(
                createdRoom.getId(),
                createdRoom.getRoomNumber(),
                createdRoom.getType(),
                createdRoom.getPrice(),
                createdRoom.getHotel().getId(),
                new ArrayList<>()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoomDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable String id,
            @RequestBody RoomDto updatedRoomDto
    ) {
        Optional<Room> existingRoomOpt = roomService.getRoomById(id);

        if (existingRoomOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room existingRoom = existingRoomOpt.get();

        Hotel hotel = hotelRepository.findById(updatedRoomDto.getHotelId())
                .orElseThrow(() -> new RuntimeException(
                        "Отель не найден с ID " + updatedRoomDto.getHotelId()
                ));

        existingRoom.setRoomNumber(updatedRoomDto.getRoomNumber());
        existingRoom.setType(updatedRoomDto.getType());
        existingRoom.setPrice(updatedRoomDto.getPrice());
        existingRoom.setHotel(hotel);

        Room savedRoom = roomService.getRoomById(id).orElseThrow();

        RoomDto updatedRoomDtoResult = new RoomDto(
                savedRoom.getId(),
                savedRoom.getRoomNumber(),
                savedRoom.getType(),
                savedRoom.getPrice(),
                savedRoom.getHotel().getId(),
                savedRoom.getFacilities().stream()
                        .map(facility -> new FacilityDto(facility.getId(), facility.getName()))
                        .toList()
        );

        return ResponseEntity.ok(updatedRoomDtoResult);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        if (roomService.getRoomById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}