package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelRestController {
    private final HotelService hotelService;

    @GetMapping
    public List<HotelEntity> getHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category) {
        return hotelService.getHotels(city, category);
    }

    @GetMapping("/{id}")
    public Optional<HotelEntity> getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }
}
