package com.example.hotelbookingv2.service.impl;

import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.service.HotelService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<HotelEntity> getHotels(String city, String category) {
        return (city != null && category != null)
                ? hotelRepository.findByCityAndCategory(city, category)
                : hotelRepository.findAll();
    }

    @Override
    public List<HotelEntity> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
    }

    @Override
    public List<HotelEntity> getHotelsByCategory(String category) {
        return hotelRepository.findByCategory(category);
    }

    @Override
    public List<HotelEntity> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public Optional<HotelEntity> getHotelById(String id) {
        return hotelRepository.findById(id);
    }

    @Override
    public HotelEntity saveHotel(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(String id) {
        hotelRepository.deleteById(id);
    }

    @Override
    public HotelEntity updateHotel(String id, HotelEntity updatedHotel) {
        return hotelRepository.findById(id).map(existingHotel -> {
            existingHotel.setName(updatedHotel.getName());
            existingHotel.setCity(updatedHotel.getCity());
            existingHotel.setCategory(updatedHotel.getCategory());
            existingHotel.setAvailableFromDate(updatedHotel.getAvailableFromDate());

            if (updatedHotel.getRooms() != null && !updatedHotel.getRooms().isEmpty()) {
                existingHotel.getRooms().clear();
                existingHotel.getRooms().addAll(updatedHotel.getRooms());
            }

            return hotelRepository.save(existingHotel);
        }).orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }
}



