package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.LikeStatusDto;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "API для управления понравившимися отелями и номерами")
public class FavoriteController {

    private final AccountService accountService;

    // ОТЕЛИ

    @Operation(summary = "Добавить отель в избранное", description = "Добавляет отель в список понравившихся")
    @PostMapping("/hotels/{hotelId}")
    public ResponseEntity<LikeStatusDto> addHotelToFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String hotelId) {
        accountService.addLikedHotel(userId, hotelId);
        return ResponseEntity.ok(new LikeStatusDto(true, "Отель добавлен в избранное"));
    }

    @Operation(summary = "Удалить отель из избранного", description = "Удаляет отель из списка понравившихся")
    @DeleteMapping("/hotels/{hotelId}")
    public ResponseEntity<LikeStatusDto> removeHotelFromFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String hotelId) {
        accountService.removeLikedHotel(userId, hotelId);
        return ResponseEntity.ok(new LikeStatusDto(false, "Отель удален из избранного"));
    }

    @Operation(summary = "Получить понравившиеся отели", description = "Возвращает список понравившихся отелей пользователя")
    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getLikedHotels(@RequestHeader("X-User-Id") Long userId) {
        List<Hotel> likedHotels = accountService.getLikedHotels(userId);
        return ResponseEntity.ok(likedHotels);
    }

    @Operation(summary = "Проверить, находится ли отель в избранном", description = "Проверяет, добавлен ли отель в избранное")
    @GetMapping("/hotels/{hotelId}/status")
    public ResponseEntity<LikeStatusDto> isHotelLiked(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String hotelId) {
        boolean isLiked = accountService.isHotelLiked(userId, hotelId);
        String message = isLiked ? "Отель в избранном" : "Отель не в избранном";
        return ResponseEntity.ok(new LikeStatusDto(isLiked, message));
    }

    // КОМНАТЫ

    @Operation(summary = "Добавить номер в избранное", description = "Добавляет номер в список понравившихся")
    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<LikeStatusDto> addRoomToFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String roomId) {
        accountService.addLikedRoom(userId, roomId);
        return ResponseEntity.ok(new LikeStatusDto(true, "Номер добавлен в избранное"));
    }

    @Operation(summary = "Удалить номер из избранного", description = "Удаляет номер из списка понравившихся")
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<LikeStatusDto> removeRoomFromFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String roomId) {
        accountService.removeLikedRoom(userId, roomId);
        return ResponseEntity.ok(new LikeStatusDto(false, "Номер удален из избранного"));
    }

    @Operation(summary = "Получить понравившиеся номера", description = "Возвращает список понравившихся номеров пользователя")
    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getLikedRooms(@RequestHeader("X-User-Id") Long userId) {
        List<Room> likedRooms = accountService.getLikedRooms(userId);
        return ResponseEntity.ok(likedRooms);
    }

    @Operation(summary = "Проверить, находится ли номер в избранном", description = "Проверяет, добавлен ли номер в избранное")
    @GetMapping("/rooms/{roomId}/status")
    public ResponseEntity<LikeStatusDto> isRoomLiked(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String roomId) {
        boolean isLiked = accountService.isRoomLiked(userId, roomId);
        String message = isLiked ? "Номер в избранном" : "Номер не в избранном";
        return ResponseEntity.ok(new LikeStatusDto(isLiked, message));
    }

    // ТОГГЛ (переключение состояния) - удобно для фронтенда

    @Operation(summary = "Переключить состояние отеля в избранном", description = "Добавляет или удаляет отель из избранного")
    @PostMapping("/hotels/{hotelId}/toggle")
    public ResponseEntity<LikeStatusDto> toggleHotelFavorite(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String hotelId) {
        boolean currentlyLiked = accountService.isHotelLiked(userId, hotelId);

        if (currentlyLiked) {
            accountService.removeLikedHotel(userId, hotelId);
            return ResponseEntity.ok(new LikeStatusDto(false, "Отель удален из избранного"));
        } else {
            accountService.addLikedHotel(userId, hotelId);
            return ResponseEntity.ok(new LikeStatusDto(true, "Отель добавлен в избранное"));
        }
    }

    @Operation(summary = "Переключить состояние номера в избранном", description = "Добавляет или удаляет номер из избранного")
    @PostMapping("/rooms/{roomId}/toggle")
    public ResponseEntity<LikeStatusDto> toggleRoomFavorite(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String roomId) {
        boolean currentlyLiked = accountService.isRoomLiked(userId, roomId);

        if (currentlyLiked) {
            accountService.removeLikedRoom(userId, roomId);
            return ResponseEntity.ok(new LikeStatusDto(false, "Номер удален из избранного"));
        } else {
            accountService.addLikedRoom(userId, roomId);
            return ResponseEntity.ok(new LikeStatusDto(true, "Номер добавлен в избранное"));
        }
    }
}