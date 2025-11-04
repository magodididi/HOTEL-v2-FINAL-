package com.example.hotelbookingv2.mapper;

import com.example.hotelbookingv2.dto.AccountResponseDto;
import com.example.hotelbookingv2.dto.BookingDto;
import com.example.hotelbookingv2.dto.HotelDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.model.Account;
import com.example.hotelbookingv2.model.Booking;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountResponseDto toAccountResponseDto(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        dto.setId(account.getId());
        dto.setFullName(account.getFullName());
        dto.setPhone(account.getPhone());
        dto.setAddress(account.getAddress());
        dto.setBirthDate(account.getBirthDate());
        dto.setGender(account.getGender());
        dto.setPassportNumber(account.getPassportNumber());
        dto.setPassportIssueDate(account.getPassportIssueDate());
        dto.setPreferredRoomType(account.getPreferredRoomType());
        dto.setPreferredHotelCategory(account.getPreferredHotelCategory());

        // User ID
        if (account.getUser() != null) {
            dto.setUserId(account.getUser().getId());
            dto.setUsername(account.getUser().getUsername());
            dto.setEmail(account.getUser().getEmail());
            dto.setRoles(account.getUser().getRoles());
        }

        if (account.getBookings() != null) {
            dto.setBookings(account.getBookings().stream()
                    .map(this::toBookingDto)
                    .collect(Collectors.toList()));
        }

        // Маппинг коллекций
        if (account.getBookings() != null) {
            dto.setBookings(account.getBookings().stream()
                    .map(this::toBookingDto)
                    .collect(Collectors.toList()));
        }

        if (account.getLikedHotels() != null) {
            dto.setLikedHotels(account.getLikedHotels().stream()
                    .map(this::toHotelDto)
                    .collect(Collectors.toList()));
        }

        if (account.getLikedRooms() != null) {
            dto.setLikedRooms(account.getLikedRooms().stream()
                    .map(this::toRoomDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setAccountId(booking.getAccount().getId());
        dto.setHotelId(booking.getHotel().getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setGuestFullName(booking.getGuestFullName());
        dto.setGuestPhone(booking.getGuestPhone());
        dto.setGuestPassportSeries(booking.getGuestPassportSeries());
        dto.setGuestPassportNumber(booking.getGuestPassportNumber());
        dto.setStatus(booking.getStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setCreatedAt(booking.getCreatedAt());

        // Дополнительные поля для удобства
        if (booking.getHotel() != null) {
            dto.setHotelName(booking.getHotel().getName());
        }
        if (booking.getRoom() != null) {
            dto.setRoomNumber(booking.getRoom().getRoomNumber());
            dto.setRoomType(booking.getRoom().getType());
        }

        return dto;
    }

    private HotelDto toHotelDto(Hotel hotel) {
        HotelDto dto = new HotelDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setCategory(hotel.getCategory());
        dto.setAvailableFromDate(hotel.getAvailableFromDate());
        return dto;
    }

    private RoomDto toRoomDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());

        if (room.getHotel() != null) {
            dto.setHotelId(room.getHotel().getId());
        }

        return dto;
    }
}