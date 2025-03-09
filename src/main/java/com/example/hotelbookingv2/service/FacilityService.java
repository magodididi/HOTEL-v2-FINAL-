package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.model.FacilityEntity;
import com.example.hotelbookingv2.model.RoomEntity;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;

    // Добавление удобства в комнату
    @Transactional
    public void addFacilityToRoom(String roomId, String facilityId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена"));

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Facilities не найдено"));

        room.addFacility(facility);
        roomRepository.save(room);
    }

    // Удаление удобства из комнаты
    @Transactional
    public void removeFacilityFromRoom(String roomId, String facilityId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена"));

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Удобство не найдено"));

        // Проверяем, действительно ли эта комната связана с удобством
        if (!room.getFacilities().contains(facility)) {
            throw new EntityNotFoundException("Удобство не связано с этой комнатой");
        }

        room.removeFacility(facility);
        roomRepository.save(room); // Сохраняем изменения
    }


    // Удаление удобства (сначала удаляем связи)
    @Transactional
    public void deleteFacility(String facilityId) {
        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Удобство не найдено"));

        // Убираем все связи, чтобы Hibernate не выбрасывал ошибку
        facility.getRooms().forEach(room -> room.getFacilities().remove(facility));
        facilityRepository.delete(facility);
    }

    public FacilityEntity saveFacility(FacilityEntity facility) {
        return facilityRepository.save(facility);
    }
}

