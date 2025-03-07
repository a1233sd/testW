package com.ex.wallet.controller;

import com.ex.wallet.dbase.Transaction;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.service.TransactionService;
import com.ex.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    public TransactionController(TransactionService transactionService, WalletService walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactionsByWallet(@PathVariable UUID walletId) {
        Wallet wallet = walletService.getWallet(walletId);
        List<Transaction> transactions = transactionService.getTransactionsByWallet(wallet);
        return ResponseEntity.ok(transactions);
    }
}