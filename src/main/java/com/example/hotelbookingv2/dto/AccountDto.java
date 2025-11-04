package com.example.hotelbookingv2.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountDto {
    private String fullName;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String gender; // Male, Female, Other
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String preferredRoomType;
    private String preferredHotelCategory;
}
