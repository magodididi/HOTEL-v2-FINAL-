package com.example.hotelbookingv2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestFullName;
    private String guestPhone;
    private String guestPassportSeries;
    private String guestPassportNumber;
    private String specialRequests;
    private Integer numberOfGuests;
}