package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.BookingDto;
import com.example.hotelbookingv2.dto.CreateBookingRequest;
import com.example.hotelbookingv2.model.Booking;
import com.example.hotelbookingv2.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Бронирования", description = "API для управления бронированиями")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Создать бронирование", description = "Создает новое бронирование для текущего пользователя")
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateBookingRequest request) {
        BookingDto booking = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои бронирования", description = "Возвращает все бронирования текущего пользователя")
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader("X-User-Id") Long userId) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my/active")
    @Operation(summary = "Получить активные бронирования", description = "Возвращает активные бронирования текущего пользователя")
    public ResponseEntity<List<BookingDto>> getActiveUserBookings(@RequestHeader("X-User-Id") Long userId) {
        List<BookingDto> bookings = bookingService.getActiveUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Получить бронирование по ID", description = "Возвращает информацию о конкретном бронировании")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable String bookingId) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Отменить бронирование", description = "Отменяет указанное бронирование")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable String bookingId) {
        BookingDto booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/payment-status")
    @Operation(summary = "Обновить статус оплаты", description = "Обновляет статус оплаты бронирования")
    public ResponseEntity<BookingDto> updatePaymentStatus(
            @PathVariable String bookingId,
            @RequestParam Booking.PaymentStatus paymentStatus) {
        BookingDto booking = bookingService.updatePaymentStatus(bookingId, paymentStatus);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/check-availability")
    @Operation(summary = "Проверить доступность номера", description = "Проверяет, свободен ли номер на указанные даты")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam String roomId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {
        boolean isAvailable = !bookingService.isRoomOccupied(roomId,
                java.time.LocalDate.parse(checkInDate),
                java.time.LocalDate.parse(checkOutDate));
        return ResponseEntity.ok(isAvailable);
    }




    // ДОБАВИТЬ: Получить все бронирования
    @GetMapping("/all")
    @Operation(summary = "Получить все бронирования", description = "Возвращает все бронирования (для админки)")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // ДОБАВИТЬ: Обновить бронирование
    @PutMapping("/{bookingId}")
    @Operation(summary = "Обновить бронирование", description = "Обновляет информацию о бронировании")
    public ResponseEntity<BookingDto> updateBooking(
            @PathVariable String bookingId,
            @RequestBody BookingDto bookingDto) {
        BookingDto updated = bookingService.updateBooking(bookingId, bookingDto);
        return ResponseEntity.ok(updated);
    }

    // ДОБАВИТЬ: Удалить бронирование
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Удалить бронирование", description = "Удаляет бронирование")
    public ResponseEntity<Void> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }


}