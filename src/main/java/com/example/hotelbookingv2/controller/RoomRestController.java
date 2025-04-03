package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Комнаты", description = "API для управления номерами в отелях") // Описание контроллера
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    @Operation(summary = "Получить номера отеля",
            description = "Возвращает список номеров по ID отеля")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(
            @Parameter(description = "ID отеля") @PathVariable String hotelId) {
        List<Room> rooms = roomService.findRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Получить номера с удобством",
            description = "Возвращает список номеров с указанным удобством")
    @GetMapping("/facility/{facilityName}")
    public ResponseEntity<List<Room>> getRoomsByFacility(
            @Parameter(description = "Название удобства (например, WiFi, бассейн)")
            @PathVariable String facilityName) {
        List<Room> rooms = roomService.findRoomsByFacility(facilityName);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Получить номер по ID",
            description = "Возвращает информацию о номере по его ID")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(
            @Parameter(description = "ID номера") @PathVariable String id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Удалить номер", description = "Удаляет номер по его ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID номера") @PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Создать номер", description = "Создает новый номер в отеле")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto roomDto) {
        Room room = roomService.saveRoom(mapToEntity(roomDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(room));
    }

    @Operation(summary = "Обновить номер", description = "Обновляет информацию о номере по его ID")
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody RoomDto updatedRoomDto
    ) {
        Room updatedRoom = roomService.updateRoom(id, mapToEntity(updatedRoomDto));
        return ResponseEntity.ok(mapToDto(updatedRoom));
    }

    private Room mapToEntity(RoomDto dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setType(dto.getType());
        room.setPrice(dto.getPrice());

        if (dto.getFacilities() != null) {
            List<Facility> facilities = dto.getFacilities().stream()
                    .map(facilityDto -> new Facility(facilityDto.getId(), facilityDto.getName()))
                    .toList();
            room.setFacilities(new ArrayList<>(facilities));
        }
        return room;
    }

    private RoomDto mapToDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPrice(),
                room.getHotel().getId(),
                room.getFacilities().stream().map(
                        f -> new FacilityDto(f.getId(), f.getName())).toList());
    }

}
