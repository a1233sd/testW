package com.ex.wallet.DTO;

import java.util.UUID;

public class WalletCreateRequest {
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

