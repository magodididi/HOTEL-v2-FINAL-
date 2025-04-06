package com.example.hotelbookingv2;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.repository.RoomRepository;
import com.example.hotelbookingv2.repository.FacilityRepository;
import com.example.hotelbookingv2.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void findRoomsByHotel_success() {
        Room room1 = new Room();
        Room room2 = new Room();
        List<Room> rooms = Arrays.asList(room1, room2);

        Mockito.when(roomRepository.findRoomsByHotel("hotel1")).thenReturn(rooms);

        List<Room> result = roomService.findRoomsByHotel("hotel1");

        assertEquals(2, result.size());
        Mockito.verify(roomRepository).findRoomsByHotel("hotel1");
    }

    @Test
    void findRoomsByHotel_notFound() {
        Mockito.when(roomRepository.findRoomsByHotel("hotel1")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomsByHotel("hotel1"));
    }

    @Test
    void findRoomsByFacility_success() {
        Room room1 = new Room();
        Room room2 = new Room();
        List<Room> rooms = Arrays.asList(room1, room2);

        Mockito.when(roomRepository.findRoomsByFacility("WiFi")).thenReturn(rooms);

        List<Room> result = roomService.findRoomsByFacility("WiFi");

        assertEquals(2, result.size());
        Mockito.verify(roomRepository).findRoomsByFacility("WiFi");
    }

    @Test
    void findRoomsByFacility_notFound() {
        Mockito.when(roomRepository.findRoomsByFacility("WiFi")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomsByFacility("WiFi"));
    }

    @Test
    void getRoomById_success_fromCache() {
        Room room = new Room();
        Mockito.when(roomCache.get("room1")).thenReturn(room);

        Room result = roomService.getRoomById("room1");

        assertEquals(room, result);
        Mockito.verify(roomCache).get("room1");
        Mockito.verify(roomRepository, Mockito.never()).findById(Mockito.anyString());
    }

    @Test
    void getRoomById_success_fromRepository() {
        // Создаем комнату
        Room room = new Room();

        // Настроим mock поведения для кеша и репозитория
        Mockito.when(roomCache.get("room1")).thenReturn(null);  // Кеш возвращает null
        Mockito.when(roomRepository.findById("room1")).thenReturn(Optional.of(room));  // Репозиторий возвращает комнату
        Mockito.doNothing().when(roomCache).put("room1", room);  // Настроим кеш на прием комнаты

        // Когда
        Room result = roomService.getRoomById("room1");

        // Тогда
        assertEquals(room, result);  // Проверяем, что результат — это наша комната
        Mockito.verify(roomCache).get("room1");  // Проверяем, что кеш был проверен
        Mockito.verify(roomRepository).findById("room1");  // Проверяем, что репозиторий был вызван
        Mockito.verify(roomCache).put("room1", room);  // Проверяем, что put был вызван для обновления кеша
    }


    @Test
    void getRoomById_notFound() {
        Mockito.when(roomCache.get("room1")).thenReturn(null);
        Mockito.when(roomRepository.findById("room1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById("room1"));
    }

    @Test
    void saveRoom_success() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        Mockito.when(roomRepository.existsByRoomNumberAndHotelId("101", room.getHotel().getId())).thenReturn(false);
        Mockito.when(roomRepository.save(room)).thenReturn(room);
        Mockito.doNothing().when(roomCache).put(Mockito.anyString(), Mockito.any(Room.class));

        Room savedRoom = roomService.saveRoom(room);

        assertNotNull(savedRoom);
        assertEquals(room, savedRoom);
        Mockito.verify(roomRepository).save(room);
        Mockito.verify(roomCache).put(Mockito.anyString(), Mockito.any(Room.class));
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
        // Создаем комнату с номером 101
        Room room = new Room();
        room.setRoomNumber("101");
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        // Настроим mock для проверки существования комнаты в отеле
        Mockito.when(roomRepository.existsByRoomNumberAndHotelId("101", room.getHotel().getId()))
                .thenReturn(true);  // Комната с номером 101 уже существует

        // Проверяем, что выбрасывается исключение InvalidInputException
        assertThrows(InvalidInputException.class, () -> roomService.saveRoom(room));
    }


    @Test
    void deleteRoom_success() {
        Mockito.when(roomRepository.existsById("room1")).thenReturn(true);
        Mockito.doNothing().when(roomRepository).deleteById("room1");
        Mockito.doNothing().when(roomCache).remove("room1");

        roomService.deleteRoom("room1");

        Mockito.verify(roomRepository).deleteById("room1");
        Mockito.verify(roomCache).remove("room1");
    }

    @Test
    void deleteRoom_notFound() {
        Mockito.when(roomRepository.existsById("room1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> roomService.deleteRoom("room1"));
    }

    @Test
    void updateRoom_success() {
        // Given
        Room room = new Room();
        room.setId("room1");
        room.setRoomNumber("101");
        room.setPrice(100.0);
        room.setType("Single");
        room.setHotel(new Hotel());

        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        // Mock Facility Repository
        Facility facility = new Facility();
        facility.setId("facility1");
        List<Facility> facilities = List.of(facility);
        Mockito.when(facilityRepository.findAllById(Mockito.anyList())).thenReturn(facilities);

        // Mock Room Repository and Room Cache
        Mockito.when(roomRepository.findById("room1")).thenReturn(Optional.of(room));
        Mockito.when(roomRepository.save(Mockito.any(Room.class))).thenReturn(room);
        Mockito.doNothing().when(roomCache).put(Mockito.anyString(), Mockito.any(Room.class));

        // When
        Room result = roomService.updateRoom("room1", updatedRoom);

        // Then
        assertEquals("102", result.getRoomNumber());
        assertEquals(150.0, result.getPrice());
        Mockito.verify(roomRepository).save(Mockito.any(Room.class));
        Mockito.verify(roomCache).put(Mockito.anyString(), Mockito.any(Room.class));
        Mockito.verify(facilityRepository).findAllById(Mockito.anyList());  // Verify that the facilityRepository method is called
    }


    @Test
    void updateRoom_notFound() {
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(150.0);
        updatedRoom.setType("Double");

        Mockito.when(roomRepository.findById("room1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.updateRoom("room1", updatedRoom));
    }
}
