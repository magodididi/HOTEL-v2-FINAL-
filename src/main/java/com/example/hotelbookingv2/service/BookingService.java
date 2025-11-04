package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.dto.BookingDto;
import com.example.hotelbookingv2.dto.CreateBookingRequest;
import com.example.hotelbookingv2.model.*;
import com.example.hotelbookingv2.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AccountService accountService;
    private final HotelService hotelService;
    private final RoomService roomService;

    @Transactional
    public BookingDto createBooking(Long accountId, CreateBookingRequest request) {
        // Проверяем существование сущностей
        Account account = accountService.getAccountById(accountId);
        Room room = roomService.getRoomById(request.getRoomId());
        Hotel hotel = room.getHotel();

        // Проверяем доступность номера
        if (isRoomOccupied(room.getId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new IllegalArgumentException("Номер уже забронирован на указанные даты");
        }

        // Проверяем корректность дат
        if (request.getCheckInDate().isBefore(LocalDate.now()) ||
                request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new IllegalArgumentException("Некорректные даты бронирования");
        }

        // Создаем бронирование
        Booking booking = new Booking();
        booking.setAccount(account);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setGuestFullName(request.getGuestFullName());
        booking.setGuestPhone(request.getGuestPhone());
        booking.setGuestPassportSeries(request.getGuestPassportSeries());
        booking.setGuestPassportNumber(request.getGuestPassportNumber());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setNumberOfGuests(request.getNumberOfGuests());

        // Рассчитываем стоимость
        booking.calculateTotalPrice();

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
    }

    public List<BookingDto> getUserBookings(Long accountId) {
        return bookingRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getActiveUserBookings(Long accountId) {
        return bookingRepository.findActiveBookingsByAccount(accountId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));
        return convertToDto(booking);
    }

    @Transactional
    public BookingDto cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Бронирование уже отменено");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    @Transactional
    public BookingDto updatePaymentStatus(String bookingId, Booking.PaymentStatus paymentStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));

        booking.setPaymentStatus(paymentStatus);
        if (paymentStatus == Booking.PaymentStatus.PAID) {
            booking.setPaymentDate(java.time.LocalDateTime.now());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    public boolean isRoomOccupied(String roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return bookingRepository.isRoomOccupied(roomId, checkInDate, checkOutDate);
    }

    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setAccountId(booking.getAccount().getId());
        dto.setHotelId(booking.getHotel().getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setGuestFullName(booking.getGuestFullName());
        dto.setGuestPhone(booking.getGuestPhone());
        dto.setGuestPassportSeries(booking.getGuestPassportSeries());
        dto.setGuestPassportNumber(booking.getGuestPassportNumber());
        dto.setStatus(booking.getStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setCreatedAt(booking.getCreatedAt());

        // Дополнительные поля для удобства
        dto.setHotelName(booking.getHotel().getName());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        dto.setRoomType(booking.getRoom().getType());

        return dto;
    }

    // Добавьте эти методы в BookingService
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookingDto updateBooking(String bookingId, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Обновляем поля
        booking.setStatus(bookingDto.getStatus());
        booking.setPaymentStatus(bookingDto.getPaymentStatus());
        // ... другие поля

        Booking updated = bookingRepository.save(booking);
        return convertToDto(updated);
    }

    public void deleteBooking(String bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new RuntimeException("Booking not found");
        }
        bookingRepository.deleteById(bookingId);
    }

    public Long getTotalBookingsCount() {
        return bookingRepository.count();
    }

    // BookingService.java - исправьте метод
    public Long getActiveBookingsCount() {
        return bookingRepository.countByStatusIn(
                List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.PENDING)
        );
    }

    public BigDecimal getTotalRevenue() {
        return bookingRepository.getTotalRevenue();
    }

    public Map<String, Long> getBookingStatusDistribution() {
        return bookingRepository.getBookingCountByStatus();
    }

    public Map<String, BigDecimal> getMonthlyRevenue(int year) {
        return bookingRepository.getMonthlyRevenue(year);
    }


}