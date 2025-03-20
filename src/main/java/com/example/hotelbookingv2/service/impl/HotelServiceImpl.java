package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.cache.HotelCache;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.service.HotelService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelCache hotelCache;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelCache = new HotelCache();
    }

    @Override
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
        return city + ":" + category; // Генерация ключа для кэша
    }

    @Override
    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
    }

    @Override
    public Optional<Hotel> getHotelById(String id) {
        List<Hotel> cachedHotels = hotelCache.get(id);
        if (cachedHotels != null && !cachedHotels.isEmpty()) {
            return Optional.of(cachedHotels.get(0));
        }

        Optional<Hotel> hotelFromDb = hotelRepository.findById(id);
        hotelFromDb.ifPresent(hotel -> hotelCache.put(id, List.of(hotel))); // Добавляем в кеш

        return hotelFromDb;
    }

    @Override
    public Hotel saveHotel(Hotel hotel) {
        Hotel savedHotel = hotelRepository.save(hotel);
        hotelCache.put(savedHotel.getId(), List.of(savedHotel));
        return savedHotel;
    }

    @Override
    public void deleteHotel(String id) {
        hotelRepository.deleteById(id);
        hotelCache.remove(id);
    }

    @Override
    public List<Hotel> getHotelsByCategory(String category) {
        return hotelRepository.findByCategory(category);
    }

    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel updateHotel(String id, Hotel updatedHotel) {
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
        }).orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }
}

