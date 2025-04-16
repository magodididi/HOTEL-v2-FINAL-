package com.example.hotelbookingv2.dto;

import com.example.hotelbookingv2.validation.FutureOrToday;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotelDto {
    private String id;

    @NotBlank(message = "Название отеля не должно быть пустым")
    private String name;

    @NotBlank(message = "Город не должен быть пустым")
    private String city;

    @NotBlank(message = "Категория отеля не должна быть пустой")
    @Min(value = 1, message = "Категория отеля должна быть не менее 1 звезды")
    @Max(value = 5, message = "Категория отеля должна быть не более 5 звезд")
    private String category;

    @NotBlank(message = "Дата доступности не должна быть пустой")
    @FutureOrToday(message = "Дата доступности должна быть сегодняшней или в будущем")

    private String availableFromDate;

    private List<RoomDto> rooms;

    public HotelDto(String id,
                    String name,
                    String city,
                    String category,
                    String availableFromDate,
                    List<RoomDto> rooms) {
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
