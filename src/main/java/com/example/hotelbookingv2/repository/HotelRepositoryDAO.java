package com.example.hotelbookingv2.repository;


import com.example.hotelbookingv2.model.HotelEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HotelRepositoryDAO {

    // Используем список для хранения данных о гостиницах в памяти
    private final List<HotelEntity> hotels = new ArrayList<>();

    // Метод для получения всех отелей
    public List<HotelEntity> findAll() {
        return new ArrayList<>(hotels);  // Возвращаем копию списка, чтобы избежать изменений из внешнего кода
    }

    // Метод для поиска отеля по ID
    public Optional<HotelEntity> findById(Long id) {
        return hotels.stream()
                .filter(hotel -> hotel.getId().equals(id))
                .findFirst();
    }

    // Метод для поиска отелей по городу и категории
    public List<HotelEntity> findByCityAndCategory(String city, String category) {
        return hotels.stream()
                .filter(hotel -> hotel.getCity().equals(city) && hotel.getCategory().equals(category))
                .toList();
    }

    // Метод для сохранения отеля в память
    public HotelEntity save(HotelEntity hotel) {
        if (hotel.getId() == null) {
            hotel.setId((long) (hotels.size() + 1));  // Генерируем ID для нового отеля
        }
        hotels.add(hotel);  // Добавляем отель в список
        return hotel;
    }

    // Метод для удаления отеля по ID
    public void deleteById(Long id) {
        hotels.removeIf(hotel -> hotel.getId().equals(id));
    }
}

