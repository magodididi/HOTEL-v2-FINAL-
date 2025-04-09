package com.example.hotelbookingv2;

import com.example.hotelbookingv2.cache.RoomCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void findRoomsByHotel_success() {
        when(roomRepository.findRoomsByHotel("hotel-1"))
                .thenReturn(List.of(sampleRoom));

        List<Room> result = roomService.findRoomsByHotel("hotel-1");
        assertEquals(1, result.size());
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
    void saveRoomsBulk_success() {
        List<Room> inputRooms = List.of(sampleRoom);
        when(roomRepository.existsByRoomNumberAndHotelId("101", "hotel-1")).thenReturn(false);
        when(roomRepository.saveAll(anyList())).thenReturn(inputRooms);
        List<Room> result = roomService.saveRoomsBulk(inputRooms);
        assertEquals(1, result.size());
        verify(roomCache).put("room-1", sampleRoom);
    }

    @Test
    void saveRoomsBulk_invalid_throws() {
        assertThrows(InvalidInputException.class, () -> roomService.saveRoomsBulk(List.of()));
    }
}
