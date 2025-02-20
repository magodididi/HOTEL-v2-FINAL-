package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.HotelEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class HotelRepository {
    private final List<HotelEntity> hotels = new ArrayList<>();

    public HotelRepository() {
        hotels.add(new HotelEntity(1L, "Hilton", "New York", "5-star", "2025-01-01"));
        hotels.add(new HotelEntity(2L, "Marriott", "Los Angeles", "4-star", "2025-02-01"));
        hotels.add(new HotelEntity(3L, "Sheraton", "Chicago", "3-star", "2025-03-01"));
    }

    public List<HotelEntity> findAll() {
        return new ArrayList<>(hotels);
    }

    public Optional<HotelEntity> findById(Long id) {
        return hotels.stream().filter(hotel -> hotel.getId().equals(id)).findFirst();
    }

    public List<HotelEntity> findByCityAndCategory(String city, String category) {
        return hotels.stream()
                .filter(hotel -> hotel.getCity().equalsIgnoreCase(city)
                        && hotel.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public HotelEntity save(HotelEntity hotel) {
        hotel.setId((long) (hotels.size() + 1));
        hotels.add(hotel);
        return hotel;
    }

    public void deleteById(Long id) {
        hotels.removeIf(hotel -> hotel.getId().equals(id));
    }
}