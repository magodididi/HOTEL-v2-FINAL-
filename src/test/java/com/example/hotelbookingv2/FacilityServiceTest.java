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
    void createFacility_nullName() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setName(null);

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
    void getFacilityById_nullId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.getFacilityById(null));
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
    void updateFacility_nullId() {
        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("Updated Facility");

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.updateFacility(null, updateDto));
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
    void addFacilityToRoom_nullRoomId() {
        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        Mockito.lenient().when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom(null, "1"));
    }

    @Test
    void addFacilityToRoom_nullFacilityId() {
        Room room = new Room();
        room.setId("1");

        Mockito.lenient().when(roomRepository.findById("1")).thenReturn(Optional.of(room));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom("1", null));
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
    void removeFacilityFromRoom_nullRoomId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.removeFacilityFromRoom(null, "1"));
    }

    @Test
    void removeFacilityFromRoom_nullFacilityId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.removeFacilityFromRoom("1", null));
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
    void deleteFacility_nullId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.deleteFacility(null));
    }

    @Test
    void saveFacility_success() {
        Facility facility = new Facility();
        facility.setName("Wi-Fi");

        Facility savedFacility = new Facility();
        savedFacility.setId("1");
        savedFacility.setName("Wi-Fi");

        Mockito.when(facilityRepository.save(facility)).thenReturn(savedFacility);

        Facility saved = facilityService.saveFacility(facility);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals("1", saved.getId());
        Mockito.verify(facilityRepository).save(facility);
    }

    @Test
    void saveFacility_invalidName() {
        Facility facility = new Facility();
        facility.setName("");

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.saveFacility(facility));
    }

    @Test
    void createFacility_invalidInput_specialChars() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setName("Wi-Fi @123");

        Facility facility = new Facility();
        facility.setName("Wi-Fi @123");

        Facility savedFacility = new Facility();
        savedFacility.setId("1");
        savedFacility.setName("Wi-Fi @123");

        Mockito.when(facilityRepository.save(any(Facility.class))).thenReturn(savedFacility);

        FacilityDto result = facilityService.createFacility(facilityDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getId());
        Assertions.assertEquals("Wi-Fi @123", result.getName());
        Mockito.verify(facilityRepository).save(any(Facility.class));
    }

    @Test
    void addFacilityToRoom_roomNotFound() {
        // Пробуем добавить удобство в комнату, которая не существует
        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.empty());

        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        // Здесь не нужно заглушить findById для facilityRepository, так как мы не будем его вызывать в этом тесте
        // Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.addFacilityToRoom("1", "1"));
    }

    @Test
    void removeFacilityFromRoom_roomNotFound() {
        // Пробуем удалить удобство из комнаты, которая не существует
        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.empty());

        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        // Убираем лишнюю заглушку для `facilityRepository.findById("1")`, так как это не нужно в этом тесте

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.removeFacilityFromRoom("1", "1"));
    }


    @Test
    void removeFacilityFromRoom_facilityNotFound() {
        // Пробуем удалить несуществующее удобство из комнаты
        Room room = new Room();
        room.setId("1");

        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.removeFacilityFromRoom("1", "1"));
    }

    @Test
    void addFacilityToRoom_addNullFacility() {
        Room room = new Room();
        room.setId("1");

        Mockito.when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.addFacilityToRoom("1", "1"));
    }

    @Test
    void saveFacility_nullFacility() {
        Assertions.assertThrows(NullPointerException.class, () -> facilityService.saveFacility(null));
    }

    @Test
    void updateFacility_invalidFacilityName() {
        Facility existingFacility = new Facility();
        existingFacility.setId("1");
        existingFacility.setName("Wi-Fi");

        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("");

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(existingFacility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.updateFacility("1", updateDto));
    }

    @Test
    void deleteFacility_notExistingFacility() {
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.deleteFacility("1"));
    }

    // 1. Тест для createFacility
    @Test
    void createFacility_invalidName_kjh() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setName("");  // Пустое название

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.createFacility(facilityDto));
    }

    // 2. Тест для updateFacility
    @Test
    void updateFacility_invalidName_jhb() {
        Facility existingFacility = new Facility();
        existingFacility.setId("1");
        existingFacility.setName("Wi-Fi");

        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("");  // Пустое название

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(existingFacility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.updateFacility("1", updateDto));
    }

    // 3. Тест для addFacilityToRoom
    @Test
    void addFacilityToRoom_invalidRoomI_nkj() {
        Facility facility = new Facility();
        facility.setId("1");
        facility.setName("Wi-Fi");

        Mockito.lenient().when(facilityRepository.findById("1")).thenReturn(Optional.of(facility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom(null, "1"));
    }

    @Test
    void addFacilityToRoom_invalidFacilityId() {
        Room room = new Room();
        room.setId("1");

        Mockito.lenient().when(roomRepository.findById("1")).thenReturn(Optional.of(room));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom("1", null));
    }

    // 4. Тест для removeFacilityFromRoom
    @Test
    void removeFacilityFromRoom_invalidRoomId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.removeFacilityFromRoom(null, "1"));
    }

    @Test
    void removeFacilityFromRoom_invalidFacilityId_jhvh() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.removeFacilityFromRoom("1", null));
    }

    // 5. Тест для deleteFacility
    @Test
    void deleteFacility_invalidId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.deleteFacility(null));
    }

    // 6. Проверка addFacilityToRoom когда комната или удобство пустые
    @Test
    void addFacilityToRoom_nullRoomId_jkh() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom(null, "1"));
    }

    @Test
    void addFacilityToRoom_nullFacilityId_JH() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.addFacilityToRoom("1", null));
    }

    // 7. Проверка saveFacility на пустое название
    @Test
    void saveFacility_invalidNameLo() {
        Facility facility = new Facility();
        facility.setName("");  // Пустое название

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.saveFacility(facility));
    }

    // Убедитесь, что дополнительные проверки (например, null или пустые значения для ID)
    // выполняются через все публичные методы, которые используют validateId.

    // (Пример с обновлением объекта и созданием нового с неверным названием)
    @Test
    void updateFacility_invalidName_MAybe() {
        Facility existingFacility = new Facility();
        existingFacility.setId("1");
        existingFacility.setName("Wi-Fi");

        FacilityDto updateDto = new FacilityDto();
        updateDto.setName("");  // Пустое название

        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.of(existingFacility));

        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.updateFacility("1", updateDto));
    }

    @Test
    void getFacilityById_invalidId() {
        Assertions.assertThrows(InvalidInputException.class, () -> facilityService.getFacilityById(null));
    }

    @Test
    void getFacilityById_notFound_POM() {
        Mockito.when(facilityRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> facilityService.getFacilityById("1"));
    }

}
