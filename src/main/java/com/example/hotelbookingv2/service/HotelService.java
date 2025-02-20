package com.example.hotelbookingv2.service;

import java.util.List;
import java.util.Optional;
import com.example.hotelbookingv2.model.HotelEntity;

public interface HotelService {
    List<HotelEntity> getHotels(String city, String category);
    Optional<HotelEntity> getHotelById(Long id);
    HotelEntity saveHotel(HotelEntity hotel);
    void deleteHotel(Long id);
}
