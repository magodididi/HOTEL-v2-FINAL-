package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.BookingDto;
import com.example.hotelbookingv2.dto.CreateBookingRequest;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.mapper.RoomMapper;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.service.BookingService;
import com.example.hotelbookingv2.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Комнаты", description = "API для управления номерами в отелях") // Описание контроллера
@RestController
@RequestMapping("/rooms")
//@RequiredArgsConstructor

public class RoomRestController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final BookingService bookingService;

    public RoomRestController(RoomService roomService, RoomMapper roomMapper, BookingService bookingService) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
        this.bookingService = bookingService;

    }

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
        Room room = roomMapper.toEntity(roomDto);
        Room saved = roomService.saveRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomMapper.toDto(saved));
    }


    @Operation(summary = "Обновить номер", description = "Обновляет информацию о номере по его ID")
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody RoomDto updatedRoomDto
    ) {
        Room updatedRoom = roomService.updateRoom(id, roomMapper.toEntity(updatedRoomDto));
        return ResponseEntity.ok(roomMapper.toDto(updatedRoom));
    }

    @Operation(summary = "Массовое создание комнат", description = "Создает"
            + " несколько комнат одним запросом")
    @PostMapping("/bulk")
    public ResponseEntity<List<RoomDto>> createRoomsBulk(
            @Valid @RequestBody List<RoomDto> roomDtos) {

        List<Room> rooms = roomDtos.stream()
                .map(roomDto -> {
                    Room room = roomMapper.toEntity(roomDto);
                    room.setId(UUID.randomUUID().toString()); // вручную задаём ID
                    return room;
                })
                .toList();

        List<Room> savedRooms = roomService.saveRoomsBulk(rooms);

        List<RoomDto> resultDtos = savedRooms.stream()
                .map(roomMapper::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(resultDtos);
    }







    @Operation(summary = "Забронировать номер", description = "Создает бронирование для выбранного номера")
    @PostMapping("/{roomId}/book")
    public ResponseEntity<BookingDto> bookRoom(
            @Parameter(description = "ID номера") @PathVariable String roomId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateBookingRequest request) {

        // Создаем копию запроса с установленным roomId
        CreateBookingRequest bookingRequest = new CreateBookingRequest();
        bookingRequest.setRoomId(roomId);
        bookingRequest.setCheckInDate(request.getCheckInDate());
        bookingRequest.setCheckOutDate(request.getCheckOutDate());
        bookingRequest.setGuestFullName(request.getGuestFullName());
        bookingRequest.setGuestPhone(request.getGuestPhone());
        bookingRequest.setGuestPassportSeries(request.getGuestPassportSeries());
        bookingRequest.setGuestPassportNumber(request.getGuestPassportNumber());
        bookingRequest.setSpecialRequests(request.getSpecialRequests());
        bookingRequest.setNumberOfGuests(request.getNumberOfGuests());

        BookingDto booking = bookingService.createBooking(userId, bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }




}
