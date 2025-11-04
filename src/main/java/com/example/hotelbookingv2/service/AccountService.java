package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.dto.AccountDto;
import com.example.hotelbookingv2.dto.AccountResponseDto;
import com.example.hotelbookingv2.mapper.AccountMapper;
import com.example.hotelbookingv2.model.Account;
import com.example.hotelbookingv2.model.Hotel;
import com.example.hotelbookingv2.model.Room;
import com.example.hotelbookingv2.model.User;
import com.example.hotelbookingv2.repository.AccountRepository;
import com.example.hotelbookingv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final AccountMapper accountMapper;

    // Создать аккаунт для пользователя
    public Account createAccount(Long userId, AccountDto accountDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (accountRepository.existsByUser(user)) {
            throw new RuntimeException("Account already exists for this user");
        }

        Account account = new Account();
        account.setUser(user);
        updateAccountFields(account, accountDto);

        return accountRepository.save(account);
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }

    // Обновить данные аккаунта
    public Account updateAccount(Long accountId, AccountDto accountDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        updateAccountFields(account, accountDto);

        return accountRepository.save(account);
    }

    // Получить аккаунт по ID пользователя (возвращаем DTO)
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found for this user"));

        return accountMapper.toAccountResponseDto(account);
    }

    // Получить все аккаунты (возвращаем DTO)
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toAccountResponseDto)
                .collect(Collectors.toList());
    }

    // Остальные методы остаются без изменений, но обновляем типы возвращаемых значений для liked методов
    @Transactional(readOnly = true)
    public List<Hotel> getLikedHotels(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getLikedHotels();
    }

    @Transactional(readOnly = true)
    public List<Room> getLikedRooms(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getLikedRooms();
    }

    // Вспомогательный метод для обновления полей
    private void updateAccountFields(Account account, AccountDto dto) {
        account.setFullName(dto.getFullName());
        account.setPhone(dto.getPhone());
        account.setAddress(dto.getAddress());
        account.setBirthDate(dto.getBirthDate());
        account.setGender(dto.getGender());
        account.setPassportNumber(dto.getPassportNumber());
        account.setPassportIssueDate(dto.getPassportIssueDate());
        account.setPreferredRoomType(dto.getPreferredRoomType());
        account.setPreferredHotelCategory(dto.getPreferredHotelCategory());
    }

    // Остальные методы остаются без изменений...
    public void checkAccountExists(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!accountRepository.existsByUser(user)) {
            throw new RuntimeException("Account must be created before booking");
        }
    }

    public Account addLikedHotel(Long accountId, String hotelId) {
        Account account = getAccountById(accountId);
        Hotel hotel = hotelService.getHotelById(hotelId);

        if (!account.getLikedHotels().contains(hotel)) {
            account.getLikedHotels().add(hotel);
            return accountRepository.save(account);
        }
        return account;
    }

    public Account removeLikedHotel(Long accountId, String hotelId) {
        Account account = getAccountById(accountId);
        Hotel hotel = hotelService.getHotelById(hotelId);

        account.getLikedHotels().remove(hotel);
        return accountRepository.save(account);
    }

    public Account addLikedRoom(Long accountId, String roomId) {
        Account account = getAccountById(accountId);
        Room room = roomService.getRoomById(roomId);

        if (!account.getLikedRooms().contains(room)) {
            account.getLikedRooms().add(room);
            return accountRepository.save(account);
        }
        return account;
    }

    public Account removeLikedRoom(Long accountId, String roomId) {
        Account account = getAccountById(accountId);
        Room room = roomService.getRoomById(roomId);

        account.getLikedRooms().remove(room);
        return accountRepository.save(account);
    }

    public boolean isHotelLiked(Long accountId, String hotelId) {
        Account account = getAccountById(accountId);
        return account.getLikedHotels().stream()
                .anyMatch(hotel -> hotel.getId().equals(hotelId));
    }

    public boolean isRoomLiked(Long accountId, String roomId) {
        Account account = getAccountById(accountId);
        return account.getLikedRooms().stream()
                .anyMatch(room -> room.getId().equals(roomId));
    }

    public void deleteAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(accountId);
    }

    // AccountService.java - добавить методы
    public Long getTotalUsersCount() {
        return userRepository.count();
    }

    public Long getUsersCountByRole(String role) {
        return userRepository.countByRolesContaining(role);
    }
}