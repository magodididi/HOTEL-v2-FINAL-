package com.example.hotelbookingv2.service.impl;


import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.service.HotelService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InMemoryHotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public List<HotelEntity> getHotels(String city, String category) {
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

