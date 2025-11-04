package com.example.hotelbookingv2.controller;

import com.example.hotelbookingv2.dto.AccountDto;
import com.example.hotelbookingv2.dto.AccountResponseDto;
import com.example.hotelbookingv2.model.Account;
import com.example.hotelbookingv2.service.AccountService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.getUserId(), request.getAccountDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @Data
    public static class CreateAccountRequest {
        private Long userId;
        private AccountDto accountDto;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable Long userId) {
        AccountResponseDto account = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId,
                                                 @RequestBody AccountDto accountDto) {
        Account updated = accountService.updateAccount(accountId, accountDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        List<AccountResponseDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}