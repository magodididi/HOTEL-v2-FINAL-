package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.HotelEntity;
import java.util.List;
import java.util.Optional;

public interface HotelService {
    List<HotelEntity> getHotels(String city, String category);

    Optional<HotelEntity> getHotelById(Long id);

    HotelEntity saveHotel(HotelEntity hotel);

    void deleteHotel(Long id);
}
