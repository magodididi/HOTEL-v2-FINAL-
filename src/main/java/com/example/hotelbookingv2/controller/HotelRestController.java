package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.HotelDto;
import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.service.HotelConverterService;
import com.example.hotelbookingv2.service.HotelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelRestController {

    private final HotelService hotelService;
    private final HotelConverterService hotelConverterService;
    private static final Logger logger = LoggerFactory.getLogger(HotelRestController.class);

    @GetMapping
    public List<HotelDto> getHotels(@RequestParam(required = false) String city,
                                    @RequestParam(required = false) String category) {
        List<Hotel> hotels = hotelService.getHotels(city, category);
        return hotels.stream()
                .map(hotelConverterService::convertToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable String id) {
        Hotel hotel = hotelService.getHotelById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отель с ID " + id + " не найден"));
        return ResponseEntity.ok(hotelConverterService.convertToDto(hotel));
    }

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto) {
        try {
            Hotel hotel = hotelConverterService.convertToEntity(hotelDto);
            Hotel createdHotel = hotelService.saveHotel(hotel);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(hotelConverterService.convertToDto(createdHotel));
        } catch (RuntimeException e) {
            logger.error("Ошибка при создании отеля", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(
            @PathVariable String id,
            @RequestBody HotelDto updatedHotelDto
    ) {
        if (hotelService.getHotelById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Hotel updatedHotelEntity = hotelConverterService.convertToEntity(updatedHotelDto);
            updatedHotelEntity.setId(id);
            Hotel updatedHotel = hotelService.updateHotel(id, updatedHotelEntity);
            return ResponseEntity.ok(hotelConverterService.convertToDto(updatedHotel));
        } catch (RuntimeException e) {
            logger.error("Ошибка при обновлении отеля с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id) {
        if (hotelService.getHotelById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
