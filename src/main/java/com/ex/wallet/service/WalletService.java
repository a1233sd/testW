package com.ex.wallet.service;

import com.ex.wallet.dbase.OperationType;
import com.ex.wallet.dbase.Transaction;
import com.ex.wallet.dbase.User;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.exception.InsufficientFundsException;
import com.ex.wallet.exception.WalletNotFoundException;
import com.ex.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;


@Service
public class WalletService {
    private  final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Wallet getWallet(UUID walletId) throws WalletNotFoundException {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден"));
    }
    public BigDecimal getBalance(UUID walletId) throws WalletNotFoundException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден"));
        return wallet.getBalance();
    }
    @Transactional(noRollbackFor = InsufficientFundsException.class)
    public Wallet updateWallet(UUID walletId, OperationType operationType, BigDecimal amount) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId);

        Transaction transaction = transactionService.createTransaction(wallet, operationType, amount);

        if (operationType == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance().add(amount));
            transaction.setSuccess(true);
            transaction.setDescription("Пополнение выполнено успешно");
        } else if (operationType == OperationType.WITHDRAW) {
            if (wallet.getBalance().compareTo(amount) < 0) {
                transaction.setSuccess(false);
                transaction.setDescription("Недостаточно средств");
                transactionService.saveTransaction(transaction);
                throw new InsufficientFundsException("Недостаточно средств в кошельке " + walletId);
            }
            wallet.setBalance(wallet.getBalance().subtract(amount));
            transaction.setSuccess(true);
            transaction.setDescription("Вывод средств успешен");
        }

        // Добавляем транзакцию в список транзакций кошелька
        wallet.getTransactions().add(transaction);
        walletRepository.save(wallet);
        return wallet;
    }
    @Transactional
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(0.0));
        wallet.setUser(user);
        // Добавляем кошелек в список кошельков юзера
        user.getWallets().add(wallet);
        return walletRepository.save(wallet);
    }
}
