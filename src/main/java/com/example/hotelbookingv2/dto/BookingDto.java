package com.example.hotelbookingv2.dto;

import com.example.hotelbookingv2.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private String id;
    private Long accountId;
    private String hotelId;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestFullName;
    private String guestPhone;
    private String guestPassportSeries;
    private String guestPassportNumber;
    private Booking.BookingStatus status;
    private Booking.PaymentStatus paymentStatus;
    private Double totalPrice;
    private String specialRequests;
    private Integer numberOfGuests;
    private LocalDateTime createdAt;

    // Дополнительные поля для удобства отображения
    private String hotelName;
    private String roomNumber;
    private String roomType;
}