package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.cache.HotelCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
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
    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
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

    @Override
    public Optional<Hotel> getHotelById(String id) {
        List<Hotel> cachedHotels = hotelCache.get(id);
        if (cachedHotels != null && !cachedHotels.isEmpty()) {
            return Optional.of(cachedHotels.get(0));
        }

        return hotelRepository.findById(id);
    }

    @Override
    public Hotel saveHotel(Hotel hotel) {
        if (hotel.getName() == null || hotel.getName().isBlank()) {
            throw new InvalidInputException("Название отеля не должно быть пустым");
        }
        Hotel savedHotel = hotelRepository.save(hotel);
        hotelCache.put(savedHotel.getId(), List.of(savedHotel));
        return savedHotel;
    }

    @Override
    public void deleteHotel(String id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Отель с ID " + id + " не найден");
        }
        hotelRepository.deleteById(id);
        hotelCache.remove(id);
    }

    @Override
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
