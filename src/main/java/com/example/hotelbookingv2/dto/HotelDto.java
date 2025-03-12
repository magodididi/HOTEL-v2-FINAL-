package com.example.hotelbookingv2.dto;

import java.util.List;

public class HotelDto {
    private String id;
    private String name;
    private String city;
    private String category;
    private String availableFromDate;
    private List<RoomDto> rooms;

    public HotelDto(
            String id,
            String name,
            String city,
            String category,
            String availableFromDate,
            List<RoomDto> rooms
    ) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.category = category;
        this.availableFromDate = availableFromDate;
        this.rooms = rooms;
    }

    public String getAvailableFromDate() {
        return availableFromDate;
    }

    public void setAvailableFromDate(String availableFromDate) {
        this.availableFromDate = availableFromDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<RoomDto> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDto> rooms) {
        this.rooms = rooms;
    }
}
