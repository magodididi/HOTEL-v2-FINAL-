package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.Hotel;
import java.util.List;
import java.util.Optional;

public interface HotelService {

    List<Hotel> getHotels(String city, String category);

    List<Hotel> getHotelsByCity(String city);

    List<Hotel> getHotelsByCategory(String category);

    List<Hotel> getAllHotels();

    Optional<Hotel> getHotelById(String id);

    Hotel saveHotel(Hotel hotel);

    void deleteHotel(String id);

    Hotel updateHotel(String id, Hotel updatedHotel);
}
