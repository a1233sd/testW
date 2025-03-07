package com.ex.wallet.controller;

import com.ex.wallet.DTO.WalletCreateRequest;
import com.ex.wallet.DTO.WalletUpdateRequest;
import com.ex.wallet.dbase.OperationType;
import com.ex.wallet.dbase.User;
import com.ex.wallet.dbase.Wallet;
import com.ex.wallet.exception.InsufficientFundsException;
import com.ex.wallet.exception.WalletNotFoundException;
import com.ex.wallet.repository.UserRepository;
import com.ex.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private UserRepository userRepository; // т.к. используется в createWallet

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(1500);
        Mockito.when(walletService.getBalance(walletId)).thenReturn(balance);

        mockMvc.perform(get("/api/v1/balance/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().string(balance.toString()));
    }

    @Test
    public void testUpdateWallet_Deposit() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletUpdateRequest req = new WalletUpdateRequest();
        req.setWalletId(walletId);
        req.setOperationType(OperationType.DEPOSIT);
        req.setAmount(BigDecimal.valueOf(500));

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(2000));

        Mockito.when(walletService.updateWallet(walletId, OperationType.DEPOSIT, BigDecimal.valueOf(500)))
                .thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("2000"));
    }

    @Test
    public void testCreateWallet() throws Exception {
        WalletCreateRequest req = new WalletCreateRequest();
        UUID userId = UUID.randomUUID();
        req.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setName("Bob");
        user.setSurname("Marley");
        user.setPhoneNumber("111222333");

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);

        Mockito.when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        Mockito.when(walletService.createWallet(user)).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testUpdateWallet_Withdraw_InsufficientFunds() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletUpdateRequest req = new WalletUpdateRequest();
        req.setWalletId(walletId);
        req.setOperationType(OperationType.WITHDRAW);
        req.setAmount(BigDecimal.valueOf(1000));

        Mockito.when(walletService.updateWallet(walletId, OperationType.WITHDRAW, BigDecimal.valueOf(1000)))
                .thenThrow(new InsufficientFundsException("Недостаточно средств"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Недостаточно средств"));
    }
}
