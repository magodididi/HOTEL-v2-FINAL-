package com.example.hotelbookingv2;

import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.cache.HotelCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock private HotelRepository hotelRepository;
    @Mock private HotelCache hotelCache;
    @InjectMocks private HotelService hotelService;

    private Hotel sampleHotel;

    @BeforeEach
    void setUp() {
        sampleHotel = new Hotel();
        sampleHotel.setId("hotel-1");
        sampleHotel.setName("Sample Hotel");
        sampleHotel.setCity("Paris");
        sampleHotel.setCategory("Luxury");
        sampleHotel.setAvailableFromDate(LocalDate.now().toString());
    }

    @Test
    void getHotels_fromCache() {
        String key = "Paris:Luxury";
        when(hotelCache.get(key)).thenReturn(List.of(sampleHotel));

        List<Hotel> result = hotelService.getHotels("Paris", "Luxury");

        assertEquals(1, result.size());
        verify(hotelRepository, never()).findByCityAndCategory(any(), any());
    }

    @Test
    void getHotels_byCityAndCategory() {
        when(hotelCache.get("Paris:Luxury")).thenReturn(null);
        when(hotelRepository.findByCityAndCategory("Paris", "Luxury"))
                .thenReturn(List.of(sampleHotel));

        List<Hotel> result = hotelService.getHotels("Paris", "Luxury");

        assertEquals(List.of(sampleHotel), result);
        verify(hotelCache).put("Paris:Luxury", List.of(sampleHotel));
    }

    @Test
    void getHotels_byCityOnly() {
        when(hotelCache.get("Paris:null")).thenReturn(null);
        when(hotelRepository.findByCity("Paris")).thenReturn(List.of(sampleHotel));

        List<Hotel> result = hotelService.getHotels("Paris", null);

        assertEquals(1, result.size());
    }

    @Test
    void getHotels_byCategoryOnly() {
        when(hotelCache.get("null:Luxury")).thenReturn(null);
        when(hotelRepository.findByCategory("Luxury")).thenReturn(List.of(sampleHotel));

        List<Hotel> result = hotelService.getHotels(null, "Luxury");

        assertEquals(1, result.size());
    }

    @Test
    void getHotels_all() {
        when(hotelCache.get("null:null")).thenReturn(null);
        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));

        List<Hotel> result = hotelService.getHotels(null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getHotelById_fromCache() {
        when(hotelCache.get("hotel-1")).thenReturn(List.of(sampleHotel));

        Hotel result = hotelService.getHotelById("hotel-1");

        assertEquals(sampleHotel, result);
        verify(hotelRepository, never()).findById(any());
    }

    @Test
    void getHotelById_fromRepo() {
        when(hotelCache.get("hotel-1")).thenReturn(null);
        when(hotelRepository.findById("hotel-1")).thenReturn(Optional.of(sampleHotel));

        Hotel result = hotelService.getHotelById("hotel-1");

        assertEquals(sampleHotel, result);
    }

    @Test
    void getHotelById_notFound() {
        when(hotelCache.get("hotel-1")).thenReturn(null);
        when(hotelRepository.findById("hotel-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.getHotelById("hotel-1"));
    }

    @Test
    void saveHotel_success() {
        when(hotelRepository.save(sampleHotel)).thenReturn(sampleHotel);

        Hotel result = hotelService.saveHotel(sampleHotel);

        assertEquals(sampleHotel, result);
        verify(hotelCache).put("hotel-1", List.of(sampleHotel));
    }

    @Test
    void saveHotel_invalidName() {
        sampleHotel.setName(" ");

        assertThrows(InvalidInputException.class,
                () -> hotelService.saveHotel(sampleHotel));
    }

    @Test
    void deleteHotel_success() {
        when(hotelRepository.existsById("hotel-1")).thenReturn(true);

        hotelService.deleteHotel("hotel-1");

        verify(hotelRepository).deleteById("hotel-1");
        verify(hotelCache).remove("hotel-1");
    }

    @Test
    void deleteHotel_notFound() {
        when(hotelRepository.existsById("hotel-1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.deleteHotel("hotel-1"));
    }

    @Test
    void updateHotel_success() {
        Hotel updated = new Hotel();
        updated.setName("Updated Hotel");
        updated.setCity("London");
        updated.setCategory("Business");
        updated.setAvailableFromDate(LocalDate.now().toString());
        updated.setRooms(List.of());  // можно с комнатами, если нужно

        when(hotelRepository.findById("hotel-1")).thenReturn(Optional.of(sampleHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(updated);

        Hotel result = hotelService.updateHotel("hotel-1", updated);

        assertEquals("Updated Hotel", result.getName());
        verify(hotelCache).put("hotel-1", List.of(updated));
    }

    @Test
    void updateHotel_missingName() {
        Hotel updated = new Hotel();
        updated.setName(" ");
        updated.setCity("City");
        updated.setCategory("Category");

        assertThrows(InvalidInputException.class,
                () -> hotelService.updateHotel("hotel-1", updated));
    }

    @Test
    void updateHotel_notFound() {
        when(hotelRepository.findById("hotel-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.updateHotel("hotel-1", sampleHotel));
    }

    @Test
    void updateHotel_invalidCity() {
        Hotel updated = new Hotel();
        updated.setName("Updated Hotel");
        updated.setCity("");  // Invalid city
        updated.setCategory("Business");

        assertThrows(InvalidInputException.class,
                () -> hotelService.updateHotel("hotel-1", updated));
    }

    @Test
    void updateHotel_invalidCategory() {
        Hotel updated = new Hotel();
        updated.setName("Updated Hotel");
        updated.setCity("London");
        updated.setCategory("");  // Invalid category

        assertThrows(InvalidInputException.class,
                () -> hotelService.updateHotel("hotel-1", updated));
    }


}
