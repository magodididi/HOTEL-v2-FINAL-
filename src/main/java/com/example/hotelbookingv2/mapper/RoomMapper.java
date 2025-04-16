package com.example.hotelbookingv2.mapper;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.service.HotelService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    private final HotelService hotelService;

    @Autowired
    public RoomMapper(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public Room toEntity(RoomDto dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setType(dto.getType());
        room.setPrice(dto.getPrice());

        Hotel hotel = hotelService.getHotelById(dto.getHotelId());
        room.setHotel(hotel);

        if (dto.getFacilities() != null) {
            List<Facility> facilities = dto.getFacilities().stream()
                    .map(facilityDto -> new Facility(facilityDto.getId(), facilityDto.getName()))
                    .toList();
            room.setFacilities(new ArrayList<>(facilities));
        }

        return room;
    }

    public RoomDto toDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPrice(),
                room.getHotel().getId(),
                room.getFacilities().stream()
                        .map(f -> new FacilityDto(f.getId(), f.getName()))
                        .toList()
        );
    }

    public List<RoomDto> toDtoList(List<Room> rooms) {
        return rooms.stream().map(this::toDto).toList();
    }

    public List<Room> toEntityList(List<RoomDto> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }
}
