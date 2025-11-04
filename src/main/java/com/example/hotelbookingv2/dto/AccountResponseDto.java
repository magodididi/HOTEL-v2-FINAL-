// AccountResponseDto.java
package com.example.hotelbookingv2.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class AccountResponseDto {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String gender;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String preferredRoomType;
    private String preferredHotelCategory;
    private List<BookingDto> bookings = new ArrayList<>();
    private List<HotelDto> likedHotels = new ArrayList<>();
    private List<RoomDto> likedRooms = new ArrayList<>();

    // Добавляем поля пользователя
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
}