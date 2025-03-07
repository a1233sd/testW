package com.ex.wallet.repository;

import com.ex.wallet.dbase.Transaction;
import com.ex.wallet.dbase.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByWallet(Wallet wallet);
}
