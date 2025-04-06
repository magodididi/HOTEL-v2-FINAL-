package com.example.hotelbookingv2;

import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.repository.HotelRepository;
import com.example.hotelbookingv2.cache.HotelCache;
import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.service.HotelService;
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

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelCache hotelCache;

    @Test
    void getHotels_success() {
        String city = "New York";
        String category = "Luxury";

        Hotel hotel1 = new Hotel("1", "Hotel A", city, category, "2023-12-01");
        Hotel hotel2 = new Hotel("2", "Hotel B", city, category, "2023-12-01");
        List<Hotel> hotels = Arrays.asList(hotel1, hotel2);

        String cacheKey = city + ":" + category;
        Mockito.when(hotelCache.get(cacheKey)).thenReturn(null);
        Mockito.when(hotelRepository.findByCityAndCategory(city, category)).thenReturn(hotels);
        Mockito.doNothing().when(hotelCache).put(cacheKey, hotels);

        List<Hotel> result = hotelService.getHotels(city, category);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(hotelRepository).findByCityAndCategory(city, category);
        Mockito.verify(hotelCache).put(cacheKey, hotels);
    }

    @Test
    void getHotels_fromCache() {
        String city = "New York";
        String category = "Luxury";
        Hotel hotel = new Hotel("1", "Hotel A", city, category, "2023-12-01");
        List<Hotel> hotels = Arrays.asList(hotel);

        String cacheKey = city + ":" + category;
        Mockito.when(hotelCache.get(cacheKey)).thenReturn(hotels);

        List<Hotel> result = hotelService.getHotels(city, category);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Hotel A", result.get(0).getName());
        Mockito.verify(hotelRepository, Mockito.never()).findByCityAndCategory(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void getHotelById_success() {
        String hotelId = "1";
        Hotel hotel = new Hotel(hotelId, "Hotel A", "New York", "Luxury", "2023-12-01");

        Mockito.when(hotelCache.get(hotelId)).thenReturn(Arrays.asList(hotel));

        Hotel result = hotelService.getHotelById(hotelId); // ✔️ просто Hotel

        Assertions.assertEquals(hotelId, result.getId());
        Mockito.verify(hotelCache).get(hotelId);
        Mockito.verify(hotelRepository, Mockito.never()).findById(hotelId);
    }


    @Test
    void getHotelById_notFound() {
        String hotelId = "1";

        Mockito.when(hotelCache.get(hotelId)).thenReturn(null);
        Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> hotelService.getHotelById(hotelId));
    }

    @Test
    void saveHotel_success() {
        Hotel hotel = new Hotel("1", "Hotel A", "New York", "Luxury", "2023-12-01");

        Mockito.when(hotelRepository.save(hotel)).thenReturn(hotel);
        Mockito.doNothing().when(hotelCache).put(hotel.getId(), List.of(hotel));

        Hotel result = hotelService.saveHotel(hotel);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Hotel A", result.getName());
        Mockito.verify(hotelRepository).save(hotel);
        Mockito.verify(hotelCache).put(hotel.getId(), List.of(hotel));
    }

    @Test
    void saveHotel_invalidName() {
        Hotel hotel = new Hotel("1", "", "New York", "Luxury", "2023-12-01");

        Assertions.assertThrows(InvalidInputException.class, () -> hotelService.saveHotel(hotel));
    }

    @Test
    void updateHotel_success() {
        String hotelId = "1";
        Hotel updatedHotel = new Hotel(hotelId, "Hotel A Updated", "New York", "Luxury", "2023-12-01");

        Hotel existingHotel = new Hotel(hotelId, "Hotel A", "New York", "Luxury", "2023-12-01");
        Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(existingHotel));
        Mockito.when(hotelRepository.save(existingHotel)).thenReturn(updatedHotel);
        Mockito.doNothing().when(hotelCache).put(hotelId, List.of(updatedHotel));

        Hotel result = hotelService.updateHotel(hotelId, updatedHotel);

        Assertions.assertEquals("Hotel A Updated", result.getName());
        Mockito.verify(hotelRepository).findById(hotelId);
        Mockito.verify(hotelRepository).save(existingHotel);
        Mockito.verify(hotelCache).put(hotelId, List.of(updatedHotel));
    }

    @Test
    void updateHotel_notFound() {
        String hotelId = "1";
        Hotel updatedHotel = new Hotel(hotelId, "Hotel A Updated", "New York", "Luxury", "2023-12-01");

        Mockito.when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> hotelService.updateHotel(hotelId, updatedHotel));
    }

    @Test
    void deleteHotel_success() {
        String hotelId = "1";
        Hotel hotel = new Hotel(hotelId, "Hotel A", "New York", "Luxury", "2023-12-01");

        Mockito.when(hotelRepository.existsById(hotelId)).thenReturn(true);
        Mockito.doNothing().when(hotelRepository).deleteById(hotelId);
        Mockito.doNothing().when(hotelCache).remove(hotelId);

        hotelService.deleteHotel(hotelId);

        Mockito.verify(hotelRepository).deleteById(hotelId);
        Mockito.verify(hotelCache).remove(hotelId);
    }

    @Test
    void deleteHotel_notFound() {
        String hotelId = "1";

        Mockito.when(hotelRepository.existsById(hotelId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> hotelService.deleteHotel(hotelId));
    }
}
