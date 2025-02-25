package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.service.HotelService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(HotelRestController.class);

    @GetMapping
    public List<HotelEntity> getHotels(@RequestParam(required = false) String city,
                                       @RequestParam(required = false) String category) {
        if (city != null && category != null) {
            return hotelService.getHotels(city, category);
        } else if (city != null) {
            return hotelService.getHotelsByCity(city);
        } else if (category != null) {
            return hotelService.getHotelsByCategory(category);
        } else {
            return hotelService.getAllHotels();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<HotelEntity> getHotelById(@PathVariable Long id) {
        HotelEntity hotel = hotelService.getHotelById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отель с ID " + id + " не найден"));
        return ResponseEntity.ok(hotel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelEntity> updateHotel(
            @PathVariable Long id,
            @RequestBody HotelEntity updatedHotel
    ) {
        Optional<HotelEntity> existingHotel = hotelService.getHotelById(id);

        if (existingHotel.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if hotel doesn't exist
        }

        try {
            return ResponseEntity.ok(hotelService.updateHotel(id, updatedHotel));
        } catch (RuntimeException e) {
            logger.error("Error updating hotel with id: {}", id, e);
            return ResponseEntity.status(500).build(); // Return 500 in case of other errors
        }
    }

    @PostMapping
    public ResponseEntity<HotelEntity> createHotel(@RequestBody HotelEntity hotel) {
        try {
            HotelEntity createdHotel = hotelService.saveHotel(hotel);
            return ResponseEntity.status(201).body(createdHotel); // Return 201 Created on success
        } catch (RuntimeException e) {
            logger.error("Error creating hotel", e);
            return ResponseEntity.badRequest().build(); // Return 400 if the hotel creation fails
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        Optional<HotelEntity> hotel = hotelService.getHotelById(id);

        if (hotel.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if hotel doesn't exist
        }

        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }
}
