package com.pichincha.dm.bank.accounts.domain;

import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {
    private UUID id;
    private UUID clientId;
    private Long accountNumber;
    private AccountType type;
    private BigDecimal initBalance;
    private BigDecimal currentBalance;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Client client;
}
