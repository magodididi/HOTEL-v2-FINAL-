package com.example.hotelbookingv2.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomDto {
    private String id; // Добавляем ID комнаты
    private String roomNumber;
    private String type;
    private Double price;
    private String hotelId;
    private List<FacilityDto> facilities;

    public RoomDto(
            String id,
            String roomNumber,
            String type,
            Double price,
            String hotelId,
            List<FacilityDto> facilities
    ) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.hotelId = hotelId;
        this.facilities = facilities;
    }

    public RoomDto(String roomNumber, String type, Double price, String hotelId) {
        this.id = UUID.randomUUID().toString(); // Генерируем ID автоматически
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.hotelId = hotelId;
        this.facilities = new ArrayList<>(); // Пустой список удобств
    }

    public RoomDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public List<FacilityDto> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<FacilityDto> facilities) {
        this.facilities = facilities;
    }
}
