package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.model.RoomEntity;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;
    private final HotelRepository hotelRepository;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomEntity> rooms = roomService.getAllRooms();

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

    @GetMapping("/hotel/{hotelId}")
    public List<RoomDto> getRoomsByHotel(@PathVariable String hotelId) {
        return roomService.getRoomsByHotel(hotelId).stream()
                .map(room -> new RoomDto(
                        room.getId(), room.getRoomNumber(), room.getType(), room.getPrice(),
                        room.getHotel().getId(),
                        room.getFacilities().stream()
                                .map(f -> new FacilityDto(
                                        f.getId(),
                                        f.getName())
                                )
                                .toList()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable String id) {
        RoomEntity room = roomService.getRoomById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Комната с ID " + id + " не найдена"
                ));

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
        RoomEntity room = roomService.getRoomById(roomId)
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

        HotelEntity hotel = hotelRepository.findById(roomCreateDto.getHotelId())
                .orElseThrow(() -> new RuntimeException(
                        "Отель не найден с ID " + roomCreateDto.getHotelId()
                ));

        RoomEntity room = new RoomEntity();
        room.setRoomNumber(roomCreateDto.getRoomNumber());
        room.setType(roomCreateDto.getType());
        room.setPrice(roomCreateDto.getPrice());
        room.setHotel(hotel);
        room.setId(UUID.randomUUID().toString());

        RoomEntity createdRoom = roomService.saveRoom(room);
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
        Optional<RoomEntity> existingRoomOpt = roomService.getRoomById(id);

        if (existingRoomOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RoomEntity existingRoom = existingRoomOpt.get();

        HotelEntity hotel = hotelRepository.findById(updatedRoomDto.getHotelId())
                .orElseThrow(() -> new RuntimeException(
                        "Отель не найден с ID " + updatedRoomDto.getHotelId()
                ));

        existingRoom.setRoomNumber(updatedRoomDto.getRoomNumber());
        existingRoom.setType(updatedRoomDto.getType());
        existingRoom.setPrice(updatedRoomDto.getPrice());
        existingRoom.setHotel(hotel);

        RoomEntity savedRoom = roomService.getRoomById(id).orElseThrow();

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