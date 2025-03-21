package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;

    @Transactional
    public List<FacilityDto> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .map(facility -> new FacilityDto(facility.getId(), facility.getName()))
                .toList();
    }

    @Transactional
    public FacilityDto updateFacility(String facilityId, FacilityDto facilityDto) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Такого удобства не найдено"));

        facility.setName(facilityDto.getName());
        facilityRepository.save(facility);

        return new FacilityDto(facility.getId(), facility.getName());
    }

    @Transactional
    public void addFacilityToRoom(String roomId, String facilityId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Facilities не найдено"));

        room.addFacility(facility);
        roomRepository.save(room);
    }

    @Transactional
    public void removeFacilityFromRoom(String roomId, String facilityId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Удобство не найдено"));

        if (!room.getFacilities().contains(facility)) {
            throw new EntityNotFoundException("Удобство не связано с этой комнатой");
        }

        room.removeFacility(facility);
        roomRepository.save(room);
    }

    @Transactional
    public void deleteFacility(String facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Удобство не найдено"));

        facility.getRooms().forEach(room -> room.getFacilities().remove(facility));
        facilityRepository.delete(facility);
    }

    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }
}

