package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.HotelDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.model.FacilityEntity;
import com.example.hotelbookingv2.model.HotelEntity;
import com.example.hotelbookingv2.model.RoomEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HotelConverterService {

    // Метод для конвертации из сущности в DTO
    public HotelDto convertToDto(HotelEntity hotel) {
        List<RoomDto> rooms = hotel.getRooms().stream()
                .map(room -> new RoomDto(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        room.getPrice(),
                        hotel.getId(),
                        room.getFacilities().stream()
                                .map(facility -> new FacilityDto(
                                        facility.getId(),
                                        facility.getName()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCategory(),
                hotel.getAvailableFromDate(), // Добавляем дату
                rooms
        );
    }

    // Метод для конвертации из DTO в сущность
    public HotelEntity convertToEntity(HotelDto hotelDto) {
        HotelEntity hotel = new HotelEntity();
        hotel.setId(hotelDto.getId());
        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCategory(hotelDto.getCategory());
        hotel.setAvailableFromDate(hotelDto.getAvailableFromDate());

        // Проверяем, переданы ли комнаты
        if (hotelDto.getRooms() != null) {
            List<RoomEntity> roomEntities = hotelDto.getRooms().stream().map(roomDto -> {
                RoomEntity room = new RoomEntity();
                room.setId(roomDto.getId());
                room.setRoomNumber(roomDto.getRoomNumber());
                room.setType(roomDto.getType());
                room.setPrice(roomDto.getPrice());
                room.setHotel(hotel);

                if (roomDto.getFacilities() != null) {
                    List<FacilityEntity> facilities = roomDto.getFacilities().stream()
                            .map(facilityDto -> {
                                FacilityEntity facility = new FacilityEntity();
                                facility.setId(facilityDto.getId());
                                facility.setName(facilityDto.getName());
                                return facility;
                            })
                            .collect(Collectors.toList());

                    room.setFacilities(facilities);
                }

                return room;
            }).collect(Collectors.toList());

            hotel.setRooms(roomEntities);
        }

        return hotel;
    }


}
