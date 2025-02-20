package com.example.hotelbookingv2.service.impl;


import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.repository.HotelRepositoryDAO;  // Репозиторий для работы с памятью
import com.example.hotelbookingv2.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InMemoryHotelServiceImpl implements HotelService {

    // Используем ваш репозиторий для работы в памяти
    private final HotelRepositoryDAO hotelRepository;

    @Override
    public List<HotelEntity> getHotels(String city, String category) {
        // Проверяем на наличие города и категории
        if (city != null && category != null) {
            return hotelRepository.findByCityAndCategory(city, category);
        }
        return hotelRepository.findAll();
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
}

