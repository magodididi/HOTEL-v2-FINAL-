package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.HotelEntity;
import java.util.List;
import java.util.Optional;

public interface HotelService {

    List<HotelEntity> getHotels(String city, String category);

    List<HotelEntity> getHotelsByCity(String city);

    List<HotelEntity> getHotelsByCategory(String category);

    List<HotelEntity> getAllHotels();

    Optional<HotelEntity> getHotelById(String id);

    HotelEntity saveHotel(HotelEntity hotel);

    void deleteHotel(String id);

    HotelEntity updateHotel(String id, HotelEntity updatedHotel);
}
