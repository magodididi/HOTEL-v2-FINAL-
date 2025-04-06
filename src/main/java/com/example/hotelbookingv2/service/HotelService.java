package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.cache.HotelCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.repository.HotelRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelCache hotelCache;

    @Autowired
    public HotelService(HotelRepository hotelRepository, HotelCache hotelCache) {
        this.hotelRepository = hotelRepository;
        this.hotelCache = hotelCache;
    }

    public List<Hotel> getHotels(String city, String category) {
        String cacheKey = generateCacheKey(city, category);

        List<Hotel> cachedHotels = hotelCache.get(cacheKey);
        if (cachedHotels != null) {
            return cachedHotels;
        }

        List<Hotel> hotels;
        if (city != null && category != null) {
            hotels = hotelRepository.findByCityAndCategory(city, category);
        } else if (city != null) {
            hotels = hotelRepository.findByCity(city);
        } else if (category != null) {
            hotels = hotelRepository.findByCategory(category);
        } else {
            hotels = hotelRepository.findAll();
        }

        hotelCache.put(cacheKey, hotels);
        return hotels;
    }

    private String generateCacheKey(String city, String category) {
        return city + ":" + category;
    }

    public Hotel getHotelById(String id) {
        List<Hotel> cachedHotels = hotelCache.get(id);
        if (cachedHotels != null && !cachedHotels.isEmpty()) {
            return cachedHotels.get(0);
        }

        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отель не найден"));
    }


    public Hotel saveHotel(Hotel hotel) {
        if (hotel.getName() == null || hotel.getName().isBlank()) {
            throw new InvalidInputException("Название отеля не должно быть пустым");
        }
        Hotel savedHotel = hotelRepository.save(hotel);
        hotelCache.put(savedHotel.getId(), List.of(savedHotel));
        return savedHotel;
    }

    public void deleteHotel(String id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Отель с ID " + id + " не найден");
        }
        hotelRepository.deleteById(id);
        hotelCache.remove(id);
    }

    public Hotel updateHotel(String id, Hotel updatedHotel) {
        if (updatedHotel.getName() == null || updatedHotel.getName().isBlank()) {
            throw new InvalidInputException("Название отеля не должно быть пустым");
        }
        if (updatedHotel.getCity() == null || updatedHotel.getCity().isBlank()) {
            throw new InvalidInputException("Город не должен быть пустым");
        }
        if (updatedHotel.getCategory() == null || updatedHotel.getCategory().isBlank()) {
            throw new InvalidInputException("Категория отеля не должна быть пустой");
        }
        return hotelRepository.findById(id).map(existingHotel -> {
            existingHotel.setName(updatedHotel.getName());
            existingHotel.setCity(updatedHotel.getCity());
            existingHotel.setCategory(updatedHotel.getCategory());
            existingHotel.setAvailableFromDate(updatedHotel.getAvailableFromDate());
            if (updatedHotel.getRooms() != null && !updatedHotel.getRooms().isEmpty()) {
                existingHotel.getRooms().clear();
                existingHotel.getRooms().addAll(updatedHotel.getRooms());
            }
            Hotel savedHotel = hotelRepository.save(existingHotel);
            hotelCache.put(id, List.of(savedHotel));
            return savedHotel;
        }).orElseThrow(() -> new ResourceNotFoundException("Отель с ID " + id + " не найден"));
    }
}
