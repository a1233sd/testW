package com.ex.wallet.controller;

import com.ex.wallet.dbase.Transaction;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.service.TransactionService;
import com.ex.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetTransactionsByWallet() throws Exception {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1500));

        Transaction t1 = new Transaction();
        t1.setId(UUID.randomUUID());
        t1.setAmount(BigDecimal.valueOf(500));
        t1.setOperationType(com.ex.wallet.dbase.OperationType.DEPOSIT);
        t1.setSuccess(true);
        t1.setDescription("Пополнение выполнено успешно");

        Transaction t2 = new Transaction();
        t2.setId(UUID.randomUUID());
        t2.setAmount(BigDecimal.valueOf(300));
        t2.setOperationType(com.ex.wallet.dbase.OperationType.WITHDRAW);
        t2.setSuccess(true);
        t2.setDescription("Вывод средств успешен");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        Mockito.when(walletService.getWallet(walletId)).thenReturn(wallet);
        Mockito.when(transactionService.getTransactionsByWallet(wallet)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
