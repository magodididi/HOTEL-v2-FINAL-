package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.exception.AlreadyExistsException;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
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
    public FacilityDto createFacility(FacilityDto facilityDto) {
        if (facilityDto.getName() == null || facilityDto.getName().isBlank()) {
            throw new InvalidInputException("Название удобства не должно быть пустым");
        }

        Facility facility = new Facility();
        facility.setName(facilityDto.getName());

        Facility savedFacility = facilityRepository.save(facility);

        return new FacilityDto(savedFacility.getId(), savedFacility.getName());
    }

    @Transactional
    public FacilityDto getFacilityById(String facilityId) {
        if (facilityId == null || facilityId.isBlank()) {
            throw new InvalidInputException("ID удобства не может быть пустым");
        }

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Удобство с ID " + facilityId + " не найдено"
                ));

        return new FacilityDto(facility.getId(), facility.getName());
    }

    @Transactional
    public List<FacilityDto> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .map(facility -> new FacilityDto(facility.getId(), facility.getName()))
                .toList();
    }

    public Facility saveFacility(Facility facility) {
        if (facility.getName() == null || facility.getName().isBlank()) {
            throw new InvalidInputException("Название не должно быть пустым");
        }
        return facilityRepository.save(facility);
    }


    private void validateId(String id, String fieldName) {
        if (id == null || id.isBlank()) {
            throw new InvalidInputException(fieldName + " не должен быть пустым");
        }
    }

    @Transactional
    public FacilityDto updateFacility(String facilityId, FacilityDto facilityDto) {
        validateId(facilityId, "ID данного удобства");

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого удобства не найдено"));

        if (facilityDto.getName() == null || facilityDto.getName().isBlank()) {
            throw new InvalidInputException("Название не должно быть пустым");
        }

        facility.setName(facilityDto.getName());
        facilityRepository.save(facility);

        return new FacilityDto(facility.getId(), facility.getName());
    }

    @Transactional
    public void addFacilityToRoom(String roomId, String facilityId) {
        validateId(roomId, "ID комнаты");
        validateId(facilityId, "ID удобства для комнаты");

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Удобство c таким id не найдено"));

        if (room.getFacilities().contains(facility)) {
            throw new AlreadyExistsException("Удобство уже добавлено в комнату");
        }

        room.addFacility(facility);
        roomRepository.save(room);
    }

    @Transactional
    public void removeFacilityFromRoom(String roomId, String facilityId) {
        validateId(roomId, "ID комнаты");
        validateId(facilityId, "ID удобства");

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Удобство не найдено"));

        if (!room.getFacilities().contains(facility)) {
            throw new ResourceNotFoundException("Удобство не связано с этой комнатой");
        }

        room.removeFacility(facility);
        roomRepository.save(room);
    }

    @Transactional
    public void deleteFacility(String facilityId) {
        if (facilityId == null || facilityId.isBlank()) {
            throw new InvalidInputException("ID удобства для удаления не может быть пустым");
        }

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Удобство с ID " + facilityId + " не найдено"
                ));

        facilityRepository.delete(facility);
    }
}

