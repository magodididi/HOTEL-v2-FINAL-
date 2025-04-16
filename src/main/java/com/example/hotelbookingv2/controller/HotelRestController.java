package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.HotelDto;
import com.example.hotelbookingv2.mapper.HotelMapper;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.service.HotelService;
import com.example.hotelbookingv2.service.VisitCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Отели", description = "Управление отелями") // Описание всего контроллера
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelRestController {

    private final HotelService hotelService;
    private final HotelMapper hotelMapper;
    private final VisitCounterService visitCounterService;

    @Operation(summary = "Получить список отелей",
            description = "Позволяет получить список отелей с фильтрацией по городу и категории")
    @GetMapping
    public ResponseEntity<List<HotelDto>> getHotels(
            @Parameter(description = "Город, в котором находится отель")
            @RequestParam(required = false) String city,
            @Parameter(description = "Категория отеля (например, 5 звезд)")
            @RequestParam(required = false) String category) {

        visitCounterService.increment(); // увеличиваем счётчик

        List<Hotel> hotels = hotelService.getHotels(city, category);
        List<HotelDto> hotelDtos = hotels.stream()
                .map(hotelMapper::convertToDto)
                .toList();
        return ResponseEntity.ok(hotelDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(
            @Parameter(description = "Идентификатор отеля") @PathVariable String id) {

        Hotel hotel = hotelService.getHotelById(id); // уже Hotel, не Optional
        HotelDto dto = hotelMapper.convertToDto(hotel);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Создать новый отель", description = "Создает и возвращает новый отель")
    @PostMapping
    public ResponseEntity<HotelDto> createHotel(
            @Parameter(description = "Данные нового отеля") @Valid @RequestBody HotelDto hotelDto) {
        Hotel hotel = hotelMapper.convertToEntity(hotelDto);
        Hotel createdHotel = hotelService.saveHotel(hotel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hotelMapper.convertToDto(createdHotel));
    }

    @Operation(summary = "Обновить информацию об отеле",
            description = "Изменяет информацию об отеле по ID")
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(
            @Parameter(description = "Идентификатор отеля")
            @PathVariable String id,
            @Parameter(description = "Обновленные данные отеля")
            @Valid @RequestBody HotelDto updatedHotelDto) {
        Hotel updatedHotelEntity = hotelMapper.convertToEntity(updatedHotelDto);
        updatedHotelEntity.setId(id);
        Hotel updatedHotel = hotelService.updateHotel(id, updatedHotelEntity);
        return ResponseEntity.ok(hotelMapper.convertToDto(updatedHotel));
    }

    @Operation(summary = "Удалить отель", description = "Удаляет отель по его ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(
            @Parameter(description = "Идентификатор отеля") @PathVariable String id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
