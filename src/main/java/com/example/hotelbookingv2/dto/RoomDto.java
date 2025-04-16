package com.example.hotelbookingv2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotNull(message = "Номер комнаты не может быть пустым")
    @Size(min = 1, message = "Номер комнаты не может быть пустым")
    private String roomNumber;

    @NotNull(message = "Тип комнаты не может быть пустым")
    private String type;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.1", message = "Цена должна быть больше 0")
    private Double price;

    @NotNull
    private String hotelId;

    private List<FacilityDto> facilities = new ArrayList<>();

    public RoomDto(String id,
                   String roomNumber,
                   String type,
                   Double price,
                   String hotelId,
                   List<FacilityDto> facilities) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.hotelId = hotelId;
        this.facilities = facilities;
    }

}
