package com.ex.wallet.controller;


import com.ex.wallet.DTO.WalletCreateRequest;
import com.ex.wallet.DTO.WalletUpdateRequest;
import com.ex.wallet.dbase.User;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.exception.InsufficientFundsException;
import com.ex.wallet.exception.WalletNotFoundException;
import com.ex.wallet.repository.UserRepository;
import com.ex.wallet.service.UserService;
import com.ex.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @GetMapping("/balance/{walletId}")
    public BigDecimal getBalance(@PathVariable UUID walletId){
        return walletService.getBalance(walletId);
    }

    @PostMapping("/wallet")
    public ResponseEntity<?> updateWallet(@RequestBody WalletUpdateRequest request) {
        try {
            Wallet wallet = walletService.updateWallet(request.getWalletId(), request.getOperationType(), request.getAmount());
            return ResponseEntity.ok(wallet);
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (InsufficientFundsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера: " + ex.getMessage());
        }
    }
    // Для существующего пользователя создаем кошелек
    @PostMapping("/wallet/create")
    public ResponseEntity<?> createWallet(@RequestBody WalletCreateRequest request) {
        try {
            // Ищем пользователя по userId
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            Wallet wallet = walletService.createWallet(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера: " + ex.getMessage());
        }
    }
}
