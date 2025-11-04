package com.example.hotelbookingv2.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Column
    private LocalDate passportIssueDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String fullName;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String gender;

    private String passportNumber;

    private String preferredRoomType;
    private String preferredHotelCategory;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Booking> bookings = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "account_liked_hotels",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id")
    )

    private List<Hotel> likedHotels = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "account_liked_rooms",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Room> likedRooms = new ArrayList<>();
}

