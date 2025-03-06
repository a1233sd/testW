package com.ex.wallet.service;

import com.ex.wallet.dbase.OperationType;
import com.ex.wallet.dbase.Transaction;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.exception.TransactionNotFoundException;
import com.ex.wallet.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransaction (UUID transactionId) throws TransactionNotFoundException {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Транзакция не найдена"));
    }

    // Поле createdDate будет установлено автоматически через @PrePersist
    public Transaction createTransaction(Wallet wallet, OperationType operationType, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setOperationType(operationType);
        transaction.setAmount(amount);
        return transaction;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
