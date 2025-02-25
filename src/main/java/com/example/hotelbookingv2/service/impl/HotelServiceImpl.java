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
        return hotelRepository.findByCity(city);  // Поиск только по городу
    }

    @Override
    public List<HotelEntity> getHotelsByCategory(String category) {
        return hotelRepository.findByCategory(category);  // Поиск только по категории
    }

    @Override
    public List<HotelEntity> getAllHotels() {
        return hotelRepository.findAll();  // Возвращаем все отели, если параметры не переданы
    }

    @Override
    public Optional<HotelEntity> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    @Override
    public HotelEntity saveHotel(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    @Override
    public HotelEntity updateHotel(Long id, HotelEntity updatedHotel) {
        return hotelRepository.findById(id).map(hotel -> {
            hotel.setName(updatedHotel.getName());
            hotel.setCity(updatedHotel.getCity());
            hotel.setCategory(updatedHotel.getCategory());
            hotel.setAvailableFromDate(updatedHotel.getAvailableFromDate());
            hotel.setRooms(updatedHotel.getRooms());
            hotel.setFacilities(updatedHotel.getFacilities());
            return hotelRepository.save(hotel);
        }).orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }
}
