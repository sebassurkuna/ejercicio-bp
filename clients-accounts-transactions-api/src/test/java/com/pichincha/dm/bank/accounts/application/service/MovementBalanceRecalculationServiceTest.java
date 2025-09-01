package com.pichincha.dm.bank.accounts.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MovementBalanceRecalculationServiceTest {

    @Mock private MovementOutputPort movementOutputPort;

    @InjectMocks private MovementBalanceRecalculationService balanceRecalculationService;

    private UUID testAccountId;
    private LocalDateTime testDateTime;
    private BigDecimal testBalanceAdjustment;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();
        testBalanceAdjustment = BigDecimal.valueOf(100);
    }

    @Test
    void givenPositiveBalanceAdjustmentWhenRecalculatePostMovementBalancesThenUpdateMovements() {
        // Arrange
        Movement movement1 =
                createTestMovement(
                        UUID.randomUUID(), BigDecimal.valueOf(500), BigDecimal.valueOf(1000));
        Movement movement2 =
                createTestMovement(
                        UUID.randomUUID(), BigDecimal.valueOf(-200), BigDecimal.valueOf(800));
        Movement movement3 =
                createTestMovement(
                        UUID.randomUUID(), BigDecimal.valueOf(300), BigDecimal.valueOf(1100));

        Flux<Movement> movementsAfterDate = Flux.just(movement1, movement2, movement3);
        List<Movement> adjustedMovements =
                List.of(
                        createAdjustedMovement(movement1, BigDecimal.valueOf(1100)),
                        createAdjustedMovement(movement2, BigDecimal.valueOf(900)),
                        createAdjustedMovement(movement3, BigDecimal.valueOf(1200)));

        doReturn(movementsAfterDate)
                .when(movementOutputPort)
                .findMovementsAfterDate(testAccountId, testDateTime);

        doReturn(Flux.fromIterable(adjustedMovements))
                .when(movementOutputPort)
                .updateBatch(any(List.class));

        // Act
        Mono<Void> result =
                balanceRecalculationService.recalculatePostMovementBalances(
                        testAccountId, testDateTime, testBalanceAdjustment);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(movementOutputPort).findMovementsAfterDate(testAccountId, testDateTime);
        verify(movementOutputPort).updateBatch(any(List.class));
    }

    @Test
    void givenZeroBalanceAdjustmentWhenRecalculatePostMovementBalancesThenSkipProcessing() {
        // Arrange
        BigDecimal zeroAdjustment = BigDecimal.ZERO;

        // Act
        Mono<Void> result =
                balanceRecalculationService.recalculatePostMovementBalances(
                        testAccountId, testDateTime, zeroAdjustment);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(movementOutputPort, never())
                .findMovementsAfterDate(any(UUID.class), any(LocalDateTime.class));
        verify(movementOutputPort, never()).updateBatch(any(List.class));
    }

    private Movement createTestMovement(
            UUID movementId, BigDecimal value, BigDecimal postMovementBalance) {
        return Movement.builder()
                .id(movementId)
                .accountId(testAccountId)
                .date(LocalDateTime.now())
                .type(
                        value.compareTo(BigDecimal.ZERO) >= 0
                                ? MovementType.CREDITO
                                : MovementType.DEBITO)
                .value(value)
                .postMovementBalance(postMovementBalance)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Movement createAdjustedMovement(
            Movement originalMovement, BigDecimal newPostMovementBalance) {
        return Movement.builder()
                .id(originalMovement.getId())
                .accountId(originalMovement.getAccountId())
                .date(originalMovement.getDate())
                .type(originalMovement.getType())
                .value(originalMovement.getValue())
                .postMovementBalance(newPostMovementBalance)
                .createdAt(originalMovement.getCreatedAt())
                .build();
    }
}
