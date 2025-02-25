package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.HotelEntity;
import java.util.List;
import java.util.Optional;

public interface HotelService {
    List<HotelEntity> getHotels(String city, String category);

    List<HotelEntity> getHotelsByCity(String city);  // новый метод для поиска по городу

    List<HotelEntity> getHotelsByCategory(String category);  // новый метод для поиска по категории

    List<HotelEntity> getAllHotels();  // метод для получения всех отелей

    Optional<HotelEntity> getHotelById(Long id);

    HotelEntity saveHotel(HotelEntity hotel);

    void deleteHotel(Long id);

    HotelEntity updateHotel(Long id, HotelEntity updatedHotel);
}
