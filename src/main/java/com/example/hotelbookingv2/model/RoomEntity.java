package com.example.hotelbookingv2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    private String roomNumber;
    private String type;
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY) // Делаем ленивую загрузку
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    private HotelEntity hotel;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "room_facilities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<FacilityEntity> facilities = new ArrayList<>();

    public void addFacility(FacilityEntity facility) {
        if (!facilities.contains(facility)) {
            facilities.add(facility);
            facility.getRooms().add(this); // ✅ Двусторонняя связь
        }
    }

    public void removeFacility(FacilityEntity facility) {
        facilities.remove(facility);
        facility.getRooms().remove(this);
    }
}





