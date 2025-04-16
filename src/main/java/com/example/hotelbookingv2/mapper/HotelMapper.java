package com.example.hotelbookingv2.mapper;

import com.example.hotelbookingv2.dto.FacilityDto;
import com.example.hotelbookingv2.dto.HotelDto;
import com.example.hotelbookingv2.dto.RoomDto;
import com.example.hotelbookingv2.model.Facility;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HotelMapper {

    public HotelDto convertToDto(Hotel hotel) {
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
                                .toList()
                ))
                .toList();

        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCategory(),
                hotel.getAvailableFromDate(),
                rooms
        );
    }

    public Hotel convertToEntity(HotelDto hotelDto) {
        Hotel hotel = new Hotel();
        hotel.setId(hotelDto.getId());
        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCategory(hotelDto.getCategory());
        hotel.setAvailableFromDate(hotelDto.getAvailableFromDate());

        if (hotelDto.getRooms() != null) {
            List<Room> roomEntities = hotelDto.getRooms().stream().map(roomDto -> {
                Room room = new Room();
                room.setId(roomDto.getId());
                room.setRoomNumber(roomDto.getRoomNumber());
                room.setType(roomDto.getType());
                room.setPrice(roomDto.getPrice());
                room.setHotel(hotel);

                if (roomDto.getFacilities() != null) {
                    List<Facility> facilities = roomDto.getFacilities().stream()
                            .map(facilityDto -> {
                                Facility facility = new Facility();
                                facility.setId(facilityDto.getId());
                                facility.setName(facilityDto.getName());
                                return facility;
                            })
                            .toList();

                    room.setFacilities(facilities);
                }

                return room;
            }).toList();

            hotel.setRooms(roomEntities);
        }
        return hotel;
    }
}
