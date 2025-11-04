package com.example.hotelbookingv2.repository;

import com.example.hotelbookingv2.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // Найти все бронирования пользователя
    List<Booking> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    // Найти бронирования по отелю
    List<Booking> findByHotelIdOrderByCreatedAtDesc(String hotelId);

    // Найти бронирования по номеру
    List<Booking> findByRoomIdOrderByCreatedAtDesc(String roomId);

    // Проверить доступность номера на даты
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status IN (com.example.hotelbookingv2.model.Booking.BookingStatus.CONFIRMED, " +
            "com.example.hotelbookingv2.model.Booking.BookingStatus.PENDING) " +
            "AND (:checkInDate BETWEEN b.checkInDate AND b.checkOutDate " +
            "OR :checkOutDate BETWEEN b.checkInDate AND b.checkOutDate " +
            "OR b.checkInDate BETWEEN :checkInDate AND :checkOutDate)")
    boolean isRoomOccupied(@Param("roomId") String roomId,
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate);

    // Найти активные бронирования пользователя
    @Query("SELECT b FROM Booking b WHERE b.account.id = :accountId " +
            "AND b.status IN (com.example.hotelbookingv2.model.Booking.BookingStatus.CONFIRMED, " +
            "com.example.hotelbookingv2.model.Booking.BookingStatus.PENDING) " +
            "ORDER BY b.checkInDate ASC")
    List<Booking> findActiveBookingsByAccount(@Param("accountId") Long accountId);

    Long countByStatusIn(List<Booking.BookingStatus> statuses);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    Map<String, Long> getBookingCountByStatus();

    @Query("SELECT FUNCTION('DATE_FORMAT', b.createdAt, '%Y-%m') as month, " +
            "SUM(b.totalPrice) as revenue " +
            "FROM Booking b " +
            "WHERE YEAR(b.createdAt) = :year AND b.paymentStatus = 'PAID' " +
            "GROUP BY FUNCTION('DATE_FORMAT', b.createdAt, '%Y-%m')")
    Map<String, BigDecimal> getMonthlyRevenue(@Param("year") int year);

}