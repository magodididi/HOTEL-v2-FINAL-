package com.example.hotelbookingv2;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.exception.AlreadyExistsException;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private RoomCache roomCache;

    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        Hotel hotel = new Hotel();
        hotel.setId("hotel-1");

        sampleRoom = new Room();
        sampleRoom.setId("room-1");
        sampleRoom.setRoomNumber("101");
        sampleRoom.setType("Deluxe");
        sampleRoom.setPrice(100.0);
        sampleRoom.setHotel(hotel);
    }

    @Test
    void findRoomsByHotel_notFound() {
        when(roomRepository.findRoomsByHotel("hotel1")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomsByHotel("hotel1"));
    }

    @Test
    void findRoomsByFacility_notFound() {
        when(roomRepository.findRoomsByFacility("WiFi")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomsByFacility("WiFi"));
    }

    @Test
    void getRoomById_success_fromCache() {
        Room room = new Room();
        when(roomCache.get("room1")).thenReturn(room);

        Room result = roomService.getRoomById("room1");

        assertEquals(room, result);
        verify(roomCache).get("room1");
        verify(roomRepository, Mockito.never()).findById(Mockito.anyString());
    }

    @Test
    void getRoomById_success_fromRepository() {
        // Создаем комнату
        Room room = new Room();

        // Настроим mock поведения для кеша и репозитория
        when(roomCache.get("room1")).thenReturn(null);  // Кеш возвращает null
        when(roomRepository.findById("room1")).thenReturn(Optional.of(room));  // Репозиторий возвращает комнату
        Mockito.doNothing().when(roomCache).put("room1", room);  // Настроим кеш на прием комнаты

        // Когда
        Room result = roomService.getRoomById("room1");

        // Тогда
        assertEquals(room, result);  // Проверяем, что результат — это наша комната
        verify(roomCache).get("room1");  // Проверяем, что кеш был проверен
        verify(roomRepository).findById("room1");  // Проверяем, что репозиторий был вызван
        verify(roomCache).put("room1", room);  // Проверяем, что put был вызван для обновления кеша
    }

    @Test
    void getRoomById_notFound() {
        when(roomCache.get("room1")).thenReturn(null);
        when(roomRepository.findById("room1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById("room1"));
    }

    @Test
    void saveRoom_invalidInput() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(-100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room));
    }

    @Test
    void saveRoom_alreadyExists() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        when(roomRepository.existsByRoomNumberAndHotelId("101", room.getHotel().getId()))
                .thenReturn(true);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room));
    }

    @Test
    void deleteRoom_notFound() {
        when(roomRepository.existsById("room1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> roomService.deleteRoom("room1"));
    }

    @Test
    void updateRoom_notFound() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        when(roomRepository.findById("room1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.updateRoom("room1", updatedRoom));
    }

    @Test
    void findRoomsByHotel_invalidId_throws() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByHotel(""));
    }

    @Test
    void findRoomsByHotel_notFound_throws() {
        when(roomRepository.findRoomsByHotel("hotel-1")).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class,
                () -> roomService.findRoomsByHotel("hotel-1"));
    }

    @Test
    void findRoomsByFacility_success() {
        when(roomRepository.findRoomsByFacility("WiFi")).thenReturn(List.of(sampleRoom));
        List<Room> result = roomService.findRoomsByFacility("WiFi");
        assertEquals(1, result.size());
    }



    @Test
    void getRoomById_fromCache() {
        when(roomCache.get("room-1")).thenReturn(sampleRoom);
        Room result = roomService.getRoomById("room-1");
        assertEquals(sampleRoom, result);
    }

    @Test
    void getRoomById_notInCache_fetchFromRepo() {
        when(roomCache.get("room-1")).thenReturn(null);
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));
        Room result = roomService.getRoomById("room-1");
        assertEquals(sampleRoom, result);
        verify(roomCache).put("room-1", sampleRoom);
    }

    @Test
    void saveRoom_success() {
        when(roomRepository.existsByRoomNumberAndHotelId("101", "hotel-1")).thenReturn(false);
        when(roomRepository.save(ArgumentMatchers.<Room>any())).thenReturn(sampleRoom);
        Room result = roomService.saveRoom(sampleRoom);
        assertEquals(sampleRoom, result);
        verify(roomCache).put("room-1", sampleRoom);
    }


    @Test
    void saveRoom_invalidInput_throws() {
        Room invalidRoom = new Room();
        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(invalidRoom));
    }

    @Test
    void deleteRoom_success() {
        when(roomRepository.existsById("room-1")).thenReturn(true);
        roomService.deleteRoom("room-1");
        verify(roomRepository).deleteById("room-1");
        verify(roomCache).remove("room-1");
    }

    @Test
    void deleteRoom_invalidId_throws() {
        assertThrows(InvalidInputException.class, () -> roomService.deleteRoom(""));
    }

    @Test
    void updateRoom_success() {
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));
        when(roomRepository.save(Mockito.<Room>any())).thenReturn(sampleRoom);
        Room update = new Room();
        update.setRoomNumber("102");
        update.setType("Suite");
        update.setPrice(200.0);

        Room result = roomService.updateRoom("room-1", update);
        assertEquals("102", result.getRoomNumber());
        verify(roomCache).put("room-1", sampleRoom);
    }

    @Test
    void updateRoom_notFound_throws() {
        when(roomRepository.findById("room-1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> roomService.updateRoom("room-1", sampleRoom));
    }

    @Test
    void findRoomsByHotel_success() {
        when(roomRepository.findRoomsByHotel("hotel-1")).thenReturn(List.of(sampleRoom));
        List<Room> result = roomService.findRoomsByHotel("hotel-1");
        assertEquals(1, result.size());
    }

    @Test
    void findRoomsByFacility_invalidFacility_throws() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByFacility(""));
    }

    @Test
    void findRoomsByHotel_invalidId_throws1() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByHotel(""));
    }

    @Test
    void findRoomsByHotel_nullId_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByHotel(null));
    }

    @Test
    void findRoomsByHotel_blankId_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByHotel(""));
    }

    @Test
    void findRoomsByFacility_nullFacility_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByFacility(null));
    }

    @Test
    void findRoomsByFacility_blankFacility_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.findRoomsByFacility(""));
    }

    @Test
    void getRoomById_nullId_throwsInvalidInputException() {
        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById(null));
    }

    @Test
    void saveRoom_invalidPrice_throwsInvalidInputException() {
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("101");
        invalidRoom.setType("Single");
        invalidRoom.setPrice(0.0);  // Неверная цена
        invalidRoom.setHotel(new Hotel());

        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(invalidRoom));
    }

    @Test
    void saveRoom_emptyRoomNumber_throwsInvalidInputException() {
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("");  // Пустой номер комнаты
        invalidRoom.setType("Single");
        invalidRoom.setPrice(100.0);
        invalidRoom.setHotel(new Hotel());

        assertThrows(NullPointerException.class, () -> roomService.saveRoom(invalidRoom));
    }

    @Test
    void saveRoom_roomAlreadyExists_throwsInvalidInputException() {
        // Создаем комнату с валидным отелем
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(100.0);
        room.setType("Single");

        Hotel hotel = new Hotel();
        hotel.setId("hotel-1");  // Устанавливаем валидный ID отеля
        room.setHotel(hotel);

        // Мокируем репозиторий, чтобы он вернул true, если комната с таким номером уже существует
        when(roomRepository.existsByRoomNumberAndHotelId("101", "hotel-1")).thenReturn(true);

        // Пытаемся сохранить комнату, ожидаем InvalidInputException
        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room));
    }


    @Test
    void deleteRoom_nullId_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.deleteRoom(null));
    }

    @Test
    void deleteRoom_blankId_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> roomService.deleteRoom(""));
    }

    @Test
    void updateRoom_validUpdate_success() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(200.0);
        updatedRoom.setType("Suite");

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        Room result = roomService.updateRoom("room-1", updatedRoom);

        assertEquals("102", result.getRoomNumber());
        assertEquals(200.0, result.getPrice());
        assertEquals("Suite", result.getType());
    }



    @Test
    void saveRoom_emptyRoomNumber_throwsInvalidInputException1() {
        Room room = new Room();
        room.setRoomNumber("");  // Пустой номер комнаты
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        assertThrows(NullPointerException.class, () -> roomService.saveRoom(room)); // Ожидаем InvalidInputException
    }

    @Test
    void saveRoom_nullRoomNumber_throwsInvalidInputException() {
        Room room = new Room();
        room.setRoomNumber(null);  // null номер комнаты
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room)); // Ожидаем InvalidInputException
    }

    @Test
    void saveRoom_negativePrice_throwsInvalidInputException() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(-100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room)); // Ожидаем InvalidInputException
    }

    @Test
    void updateRoom_success1() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));  // Мокируем существующую комнату
        when(roomRepository.save(sampleRoom)).thenReturn(sampleRoom);  // Мокируем успешное сохранение

        Room result = roomService.updateRoom("room-1", updatedRoom); // Вызываем метод updateRoom

        assertEquals("102", result.getRoomNumber());  // Проверяем, что номер был обновлен
        assertEquals(150.0, result.getPrice());  // Проверяем, что цена была обновлена
        verify(roomCache).put("room-1", sampleRoom);  // Проверяем, что кеш был обновлен
    }

    @Test
    void updateRoom_emptyRoomNumber_throwsInvalidInputException() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("");  // Пустой номер комнаты
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));  // Мокируем существующую комнату

        assertThrows(InvalidInputException.class, () -> roomService.updateRoom("room-1", updatedRoom));  // Ожидаем InvalidInputException
    }

    @Test
    void updateRoom_nullRoomNumber_throwsInvalidInputException() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber(null);  // null номер комнаты
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));  // Мокируем существующую комнату

        assertThrows(InvalidInputException.class, () -> roomService.updateRoom("room-1", updatedRoom));  // Ожидаем InvalidInputException
    }

    @Test
    void updateRoom_negativePrice_throwsInvalidInputException() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(-150.0);  // Отрицательная цена
        updatedRoom.setType("Double");

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));  // Мокируем существующую комнату

        assertThrows(InvalidInputException.class, () -> roomService.updateRoom("room-1", updatedRoom));  // Ожидаем InvalidInputException
    }

    @Test
    void updateRoom_addFacilities_success() {
        Facility facility = new Facility();
        facility.setId("wifi");
        facility.setName("WiFi");

        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(200.0);
        updatedRoom.setType("Suite");
        updatedRoom.setFacilities(List.of(facility));  // Добавляем удобство

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));  // Мокируем существующую комнату
        when(facilityRepository.findAllById(List.of("wifi"))).thenReturn(List.of(facility));  // Мокируем поиск удобства
        when(roomRepository.save(sampleRoom)).thenReturn(sampleRoom);  // Мокируем сохранение обновленной комнаты

        Room result = roomService.updateRoom("room-1", updatedRoom);  // Вызываем метод updateRoom

        assertTrue(result.getFacilities().contains(facility));  // Проверяем, что удобство было добавлено
        verify(roomCache).put("room-1", sampleRoom);  // Проверяем, что кеш был обновлен
    }

    @Test
    void updateRoom_invalidFacility_throwsInvalidInputException() {
        Facility invalidFacility = new Facility();
        invalidFacility.setId("invalid-facility");
        invalidFacility.setName("Invalid Facility");

        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(200.0);
        updatedRoom.setType("Suite");
        updatedRoom.setFacilities(List.of(invalidFacility));

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));
        when(facilityRepository.findAllById(List.of("invalid-facility"))).thenReturn(Collections.emptyList());

        assertThrows(NullPointerException.class, () -> roomService.updateRoom("room-1", updatedRoom));
    }

    @Test
    void findRoomsByFacility_noRoomsFound() {
        when(roomRepository.findRoomsByFacility("Pool")).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomsByFacility("Pool"));
    }

    @Test
    void deleteMultipleRooms_success() {
        when(roomRepository.existsById("room-1")).thenReturn(true);
        when(roomRepository.existsById("room-2")).thenReturn(true);

        roomService.deleteRoom("room-1");
        roomService.deleteRoom("room-2");

        verify(roomRepository).deleteById("room-1");
        verify(roomRepository).deleteById("room-2");
        verify(roomCache).remove("room-1");
        verify(roomCache).remove("room-2");
    }


    @Test
    void updateRoom_noChanges_success() {
        Room unchangedRoom = new Room();
        unchangedRoom.setRoomNumber("101");
        unchangedRoom.setType("Single");
        unchangedRoom.setPrice(100.0);

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(sampleRoom));
        when(roomRepository.save(sampleRoom)).thenReturn(sampleRoom);

        Room result = roomService.updateRoom("room-1", unchangedRoom);

        assertEquals("101", result.getRoomNumber());
        assertEquals("Single", result.getType());
        assertEquals(100.0, result.getPrice());
        verify(roomCache).put("room-1", sampleRoom);
    }

    @Test
    void getRoomById_cacheMiss_repositoryFailure() {
        when(roomCache.get("room-1")).thenReturn(null);
        when(roomRepository.findById("room-1")).thenReturn(Optional.empty());  // Simulate repository failure

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById("room-1"));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomsListIsEmpty() {
        // Проверка на пустой список
        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(new ArrayList<>()));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomsListIsNull() {
        // Проверка на null список
        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(null));
    }

    @Test
    void shouldThrowInvalidInputException_WhenDuplicateRoomNumbersExist() {
        // Проверка на дублирующиеся номера комнат
        Room room1 = new Room();
        room1.setRoomNumber("101");
        room1.setHotel(sampleRoom.getHotel());

        Room room2 = new Room();
        room2.setRoomNumber("101");  // Дублирующийся номер
        room2.setHotel(sampleRoom.getHotel());

        List<Room> rooms = List.of(room1, room2);

        // Должно выбросить исключение из-за дубликатов
        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowAlreadyExistsException_WhenRoomNumberAlreadyExists() {
        // Проверка на уже существующие номера комнат
        Room room1 = new Room();
        room1.setRoomNumber("101");
        room1.setHotel(sampleRoom.getHotel());

        List<Room> rooms = List.of(room1);

        when(roomRepository.existsByRoomNumberAndHotelId("101", "hotel-1")).thenReturn(true);

        // Должно выбросить исключение, если номер комнаты уже существует
        assertThrows(AlreadyExistsException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomDataIsInvalid() {
        // Проверка на некорректные данные комнаты
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("");  // Пустой номер комнаты
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(100.0);

        List<Room> rooms = List.of(invalidRoom);

        // Должно выбросить исключение из-за некорректного номера комнаты
        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldSaveRooms_WhenAllValid() {
        // Проверка на успешное сохранение данных
        Room validRoom = new Room();
        validRoom.setRoomNumber("102");
        validRoom.setHotel(sampleRoom.getHotel());
        validRoom.setType("Standard");
        validRoom.setPrice(150.0);

        List<Room> rooms = List.of(validRoom);

        when(roomRepository.saveAll(rooms)).thenReturn(rooms);

        // Сохраняем комнаты и проверяем, что данные сохранены
        List<Room> savedRooms = roomService.saveRoomsBulk(rooms);

        assertNotNull(savedRooms);
        verify(roomRepository, times(1)).saveAll(rooms);
        verify(roomCache, times(1)).put(savedRooms.get(0).getId(), savedRooms.get(0));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomNumberIsNull() {
        // Проверка на null номер комнаты
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber(null);  // Номер комнаты равен null
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(100.0);

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(NullPointerException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomNumberIsBlank() {
        // Проверка на пустой номер комнаты
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("");  // Номер комнаты пустой
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(100.0);

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenRoomTypeIsNull() {
        // Проверка на null тип комнаты
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("101");
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType(null);  // Тип комнаты равен null
        invalidRoom.setPrice(100.0);

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenPriceIsNull() {
        // Проверка на null цену
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("101");
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(null);  // Цена равна null

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenPriceIsLessThanMin() {
        // Проверка на цену меньше 0.1
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("101");
        invalidRoom.setHotel(sampleRoom.getHotel());
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(0.05);  // Цена меньше 0.1

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldThrowInvalidInputException_WhenHotelIsNull() {
        // Проверка на отсутствие отеля
        Room invalidRoom = new Room();
        invalidRoom.setRoomNumber("101");
        invalidRoom.setHotel(null);  // Отель равен null
        invalidRoom.setType("Deluxe");
        invalidRoom.setPrice(100.0);

        List<Room> rooms = List.of(invalidRoom);

        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(rooms));
    }

    @Test
    void shouldNotThrowException_WhenAllRoomDataIsValid() {
        // Проверка на все валидные данные
        Room validRoom = new Room();
        validRoom.setRoomNumber("102");
        validRoom.setHotel(sampleRoom.getHotel());
        validRoom.setType("Standard");
        validRoom.setPrice(150.0);

        List<Room> rooms = List.of(validRoom);

        when(roomRepository.saveAll(rooms)).thenReturn(rooms);

        // Проверяем, что метод не выбрасывает исключение при валидных данных
        List<Room> savedRooms = roomService.saveRoomsBulk(rooms);

        assertNotNull(savedRooms);
        verify(roomRepository, times(1)).saveAll(rooms);
        verify(roomCache, times(1)).put(savedRooms.get(0).getId(), savedRooms.get(0));
    }


}
