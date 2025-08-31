package com.pichincha.dm.bank.accounts.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface BalanceRecalculationService {

    Mono<Void> recalculatePostMovementBalances(
            UUID accountId, LocalDateTime fromDateTime, BigDecimal balanceAdjustment);
}
