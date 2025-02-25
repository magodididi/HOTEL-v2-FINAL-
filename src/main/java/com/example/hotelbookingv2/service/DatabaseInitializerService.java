package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.FacilityEntity;
import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.model.RoomEntity;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseInitializerService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;

    public DatabaseInitializerService(
            HotelRepository hotelRepository,
            RoomRepository roomRepository,
            FacilityRepository facilityRepository
    ) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
    }


    @Transactional
    public void clearDatabase() {
        roomRepository.deleteAll();
        facilityRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    @Transactional
    public void initializeDatabase() {

        HotelEntity grandHotel = new HotelEntity();
        grandHotel.setName("Grand Hotel");
        grandHotel.setCity("New York");
        grandHotel.setCategory("5-star");
        grandHotel.setAvailableFromDate("2024-01-01");

        HotelEntity cozyInn = new HotelEntity();
        cozyInn.setName("Cozy Inn");
        cozyInn.setCity("Los Angeles");
        cozyInn.setCategory("3-star");
        cozyInn.setAvailableFromDate("2024-02-15");

        // Новые отели
        HotelEntity luxResort = new HotelEntity();
        luxResort.setName("Lux Resort");
        luxResort.setCity("New York");
        luxResort.setCategory("5-star");
        luxResort.setAvailableFromDate("2024-03-10");

        HotelEntity budgetStay = new HotelEntity();
        budgetStay.setName("Budget Stay");
        budgetStay.setCity("San Francisco");
        budgetStay.setCategory("2-star");
        budgetStay.setAvailableFromDate("2024-04-05");

        hotelRepository.save(grandHotel);
        hotelRepository.save(cozyInn);
        hotelRepository.save(luxResort);
        hotelRepository.save(budgetStay);

        // Создаем комнаты и привязываем их к отелям
        RoomEntity room1 = new RoomEntity();
        room1.setRoomNumber("101");
        room1.setType("Single");
        room1.setPrice(100.0);
        room1.setHotel(grandHotel);

        RoomEntity room2 = new RoomEntity();
        room2.setRoomNumber("102");
        room2.setType("Double");
        room2.setPrice(150.0);
        room2.setHotel(grandHotel);

        RoomEntity room3 = new RoomEntity();
        room3.setRoomNumber("201");
        room3.setType("Suite");
        room3.setPrice(300.0);
        room3.setHotel(cozyInn);

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        // Создаем удобства
        FacilityEntity wifi = new FacilityEntity();
        wifi.setName("Wi-Fi");

        FacilityEntity pool = new FacilityEntity();
        pool.setName("Pool");

        FacilityEntity gym = new FacilityEntity();
        gym.setName("Gym");

        facilityRepository.save(wifi);
        facilityRepository.save(pool);
        facilityRepository.save(gym);

        // Привязываем удобства к отелям
        grandHotel.setFacilities(new ArrayList<>(List.of(wifi, pool)));
        cozyInn.setFacilities(new ArrayList<>(List.of(wifi, gym)));

        hotelRepository.save(grandHotel);
        hotelRepository.save(cozyInn);
    }
}
