package com.example.hotelbookingv2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;

    // Даты бронирования
    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    // Контактная информация (может отличаться от данных в аккаунте)
    @Column(nullable = false)
    private String guestFullName;

    @Column(nullable = false)
    private String guestPhone;

    @Column(nullable = false)
    private String guestPassportSeries;

    @Column(nullable = false)
    private String guestPassportNumber;

    // Информация об оплате
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private Double totalPrice; // Общая стоимость бронирования

    private LocalDateTime paymentDate;

    // Статус бронирования
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    // Дополнительные поля
    private String specialRequests; // Особые пожелания
    private Integer numberOfGuests; // Количество гостей

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookingStatus.CONFIRMED;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Метод для расчета общей стоимости
    public void calculateTotalPrice() {
        if (room != null && checkInDate != null && checkOutDate != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            this.totalPrice = room.getPrice() * nights;
        }
    }

    public enum BookingStatus {
        PENDING,      // Ожидание подтверждения
        CONFIRMED,    // Подтверждено
        CANCELLED,    // Отменено
        COMPLETED,    // Завершено (гость уже выехал)
        NO_SHOW       // Гость не явился
    }

    public enum PaymentStatus {
        PENDING,      // Ожидание оплаты
        PAID,         // Оплачено
        REFUNDED,     // Возвращено
        FAILED        // Ошибка оплаты
    }
}