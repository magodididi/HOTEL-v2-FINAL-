package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.exception.EntityNotFoundException;
import com.example.hotelbookingv2.model.FacilityEntity;
import com.example.hotelbookingv2.service.FacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @PutMapping("/{facilityId}")
    public ResponseEntity<FacilityDto> updateFacility(
            @PathVariable String facilityId,
            @RequestBody FacilityDto facilityDto) {
        try {
            FacilityDto updatedFacility = facilityService.updateFacility(facilityId, facilityDto);
            return ResponseEntity.ok(updatedFacility);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@RequestBody FacilityDto facilityDto) {
        FacilityEntity facility = new FacilityEntity();
        facility.setName(facilityDto.getName());
        FacilityEntity savedFacility = facilityService.saveFacility(facility);

        FacilityDto responseDto = new FacilityDto(
                savedFacility.getId(), savedFacility.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/{roomId}/add/{facilityId}")
    public ResponseEntity<Void> addFacilityToRoom(
            @PathVariable String roomId,
            @PathVariable String facilityId
    ) {
        try {
            facilityService.addFacilityToRoom(roomId, facilityId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{roomId}/remove/{facilityId}")
    public ResponseEntity<Void> removeFacilityFromRoom(
            @PathVariable String roomId,
            @PathVariable String facilityId) {
        try {
            facilityService.removeFacilityFromRoom(roomId, facilityId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{facilityId}")
    public ResponseEntity<Void> deleteFacility(@PathVariable String facilityId) {
        try {
            facilityService.deleteFacility(facilityId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
