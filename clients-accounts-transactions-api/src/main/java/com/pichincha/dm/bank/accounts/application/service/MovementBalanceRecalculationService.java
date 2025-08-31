package com.pichincha.dm.bank.accounts.application.service;

import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.domain.Movement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementBalanceRecalculationService implements BalanceRecalculationService {

    private final MovementOutputPort movementOutputPort;

    @Override
    public Mono<Void> recalculatePostMovementBalances(
            UUID accountId, LocalDateTime fromDateTime, BigDecimal balanceAdjustment) {

        if (balanceAdjustment.compareTo(BigDecimal.ZERO) == 0) {
            return Mono.empty();
        }

        return movementOutputPort
                .findMovementsAfterDate(accountId, fromDateTime)
                .map(movement -> adjustMovementBalance(movement, balanceAdjustment))
                .collectList()
                .flatMapMany(movementOutputPort::updateBatch)
                .then();
    }

    private Movement adjustMovementBalance(Movement movement, BigDecimal adjustment) {
        BigDecimal newPostMovementBalance = movement.getPostMovementBalance().add(adjustment);
        movement.setPostMovementBalance(newPostMovementBalance);
        return movement;
    }
}
