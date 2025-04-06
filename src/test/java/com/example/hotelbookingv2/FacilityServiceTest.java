package com.example.hotelbookingv2;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.exception.AlreadyExistsException;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.service.FacilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @InjectMocks
    private FacilityService facilityService;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private RoomRepository roomRepository;

    @Test
    void createFacility_success() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setName("Wi-Fi");

        Facility facility = new Facility();
        facility.setName("Wi-Fi");

        Facility savedFacility = new Facility();
        savedFacility.setId("1");
        savedFacility.setName("Wi-Fi");

        Mockito.when(facilityRepository.save(any(Facility.class))).thenReturn(savedFacility); // Используем any(Facility.class)

        FacilityDto result = facilityService.createFacility(facilityDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getId());
        Assertions.assertEquals("Wi-Fi", result.getName());
        Mockito.verify(facilityRepository).save(any(Facility.class)); // Проверяем save с любым объектом типа Facility
    }

    @Test
    void createFacility_invalidName() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setName("");

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.createFacility(facilityDto));
    }

    @Test
    void getFacilityById_success() {
        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        FacilityDto result = facilityService.getFacilityById("1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getId());
        Assertions.assertEquals("Wi-Fi", result.getName());
        Mockito.verify(facilityRepository).findById("1");
    }

    @Test
    void getFacilityById_notFound() {
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.getFacilityById("1"));
    }

    @Test
    void getAllFacilities_success() {
        Facility facilityFirst = new Facility();
        facilityFirst.setId("1");
        facilityFirst.setName("Wi-Fi");

        Facility facilitySecond = new Facility();
        facilitySecond.setId("2");
        facilitySecond.setName("AC");

        List<Facility> facilities = Arrays.asList(facilityFirst, facilitySecond);

        Mockito.when(facilityRepository.findAll()).thenReturn(facilities);

        List<FacilityDto> result = facilityService.getAllFacilities();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(facilityRepository).findAll();
    }

    @Test
    void updateFacility_success() {
        Facility existingFacility = new Facility();
        existingFacility.setId("1");
        existingFacility.setName("Wi-Fi");

        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("Premium Wi-Fi");

        Facility updatedFacility = new Facility();
        updatedFacility.setId("1");
        updatedFacility.setName("Premium Wi-Fi");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(existingFacility));
        Mockito.when(facilityRepository.save(existingFacility)).thenReturn(updatedFacility);

        FacilityDto result = facilityService.updateFacility("1", updateDto);

        Assertions.assertEquals("Premium Wi-Fi", result.getName());
        Mockito.verify(facilityRepository).findById("1");
        Mockito.verify(facilityRepository).save(existingFacility);
    }

    @Test
    void updateFacility_notFound() {
        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("Premium Wi-Fi");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.updateFacility("1", updateDto));
    }

    @Test
    void updateFacility_invalidName() {
        Facility existingFacility = new Facility();
        existingFacility.setId("1");
        existingFacility.setName("Wi-Fi");

        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(existingFacility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.updateFacility("1", updateDto));
    }

    @Test
    void addFacilityToRoom_success() {
        Room room = new Room();
        room.setId("1");

        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        facilityService.addFacilityToRoom("1", "1");

        Mockito.verify(roomRepository).findById("1");
        Mockito.verify(facilityRepository).findById("1");
        Mockito.verify(roomRepository).save(room);
    }
    @Test
    void addFacilityToRoom_alreadyExists() {
        Room room = new Room();
        room.setId("1");
        // Добавляем объект Facility в комнату
        Facility existingFacility = new Facility("1", "Wi-Fi");
        room.addFacility(existingFacility);

        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        // Мокируем возвращение комнаты и оборудования
        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        // Проверяем, что выбрасывается исключение
        Assertions.assertThrows(AlreadyExistsException.class, () -> facilityService.addFacilityToRoom("1", "1"));
    }


    @Test
    void removeFacilityFromRoom_success() {
        Room room = new Room();
        room.setId("1");
        Facility facility = new Facility("1", "Wi-Fi");

        room.addFacility(facility);

        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        facilityService.removeFacilityFromRoom("1", "1");

        Mockito.verify(roomRepository).save(room);
    }

    @Test
    void removeFacilityFromRoom_notFound() {
        Room room = new Room();
        room.setId("1");

        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.removeFacilityFromRoom("1", "1"));
    }

    @Test
    void deleteFacility_success() {
        Facility facility = new Facility("1", "Wi-Fi");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));
        Mockito.doNothing().when(facilityRepository).delete(facility);

        facilityService.deleteFacility("1");

        Mockito.verify(facilityRepository).delete(facility);
    }

    @Test
    void deleteFacility_notFound() {
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.deleteFacility("1"));
    }

    @Test
    void deleteFacility_invalidId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.deleteFacility(""));
    }
}