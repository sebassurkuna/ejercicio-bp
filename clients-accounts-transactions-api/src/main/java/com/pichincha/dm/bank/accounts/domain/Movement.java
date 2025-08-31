package com.pichincha.dm.bank.accounts.domain;

import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
public class Movement {
    private UUID id;
    private UUID accountId;
    private LocalDateTime date;
    private MovementType type;
    private BigDecimal value;
    private BigDecimal postMovementBalance;
    private LocalDateTime createdAt;
    private Account account;
}
