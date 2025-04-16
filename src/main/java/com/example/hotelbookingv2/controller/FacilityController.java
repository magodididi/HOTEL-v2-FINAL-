package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Удобства", description = "API для управления удобствами в отелях и номерах")
@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @Operation(summary = "Получить все удобства",
            description = "Возвращает список всех доступных удобств")
    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @Operation(
            summary = "Получить удобство по ID",
            description = "Возвращает удобство по его уникальному идентификатору"
    )
    @GetMapping("/{facilityId}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable String facilityId) {
        FacilityDto facility = facilityService.getFacilityById(facilityId);
        return ResponseEntity.ok(facility);
    }

    @Operation(summary = "Обновить удобство",
            description = "Обновляет информацию об удобстве по его ID")
    @PutMapping("/{facilityId}")
    public ResponseEntity<FacilityDto> updateFacility(
            @Parameter(description = "ID удобства") @PathVariable String facilityId,
            @Valid @RequestBody FacilityDto facilityDto) {
        FacilityDto updatedFacility = facilityService.updateFacility(facilityId, facilityDto);
        return ResponseEntity.ok(updatedFacility);
    }

    @Operation(
            summary = "Создать новое удобство",
            description = "Создает новое удобство и возвращает его данные"
    )
    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@RequestBody FacilityDto facilityDto) {
        FacilityDto savedFacility = facilityService.createFacility(facilityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFacility);
    }


    @Operation(summary = "Добавить удобство в номер",
            description = "Добавляет удобство в конкретный номер")
    @PostMapping("/{roomId}/add/{facilityId}")
    public ResponseEntity<Void> addFacilityToRoom(
            @Parameter(description = "ID номера") @PathVariable String roomId,
            @Parameter(description = "ID удобства") @PathVariable String facilityId) {
        facilityService.addFacilityToRoom(roomId, facilityId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить удобство из номера",
            description = "Удаляет удобство из указанного номера")
    @DeleteMapping("/{roomId}/remove/{facilityId}")
    public ResponseEntity<Void> removeFacilityFromRoom(
            @Parameter(description = "ID номера") @PathVariable String roomId,
            @Parameter(description = "ID удобства") @PathVariable String facilityId) {
        facilityService.removeFacilityFromRoom(roomId, facilityId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить удобство", description = "Удаляет удобство по его ID")
    @DeleteMapping("/{facilityId}")
    public ResponseEntity<Void> deleteFacility(
            @Parameter(description = "ID удобства") @PathVariable String facilityId) {
        facilityService.deleteFacility(facilityId);
        return ResponseEntity.noContent().build();
    }
}
