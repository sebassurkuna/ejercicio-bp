package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.MovementEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.MovementRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MovementTransactionServiceTest {

    @Mock private MovementRepository movementRepository;

    @Mock private MovementEntityMapper movementMapper;

    @InjectMocks private MovementTransactionService movementTransactionService;

    private Movement testMovement;
    private MovementEntity testMovementEntity;
    private Movement savedMovement;
    private MovementEntity savedMovementEntity;

    @BeforeEach
    void setUp() {
        UUID movementId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        testMovement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(LocalDateTime.of(2024, 1, 15, 10, 30))
                        .type(MovementType.CREDITO)
                        .value(BigDecimal.valueOf(500.00))
                        .postMovementBalance(BigDecimal.valueOf(1500.00))
                        .createdAt(LocalDateTime.of(2024, 1, 15, 10, 31))
                        .build();

        testMovementEntity =
                new MovementEntity(
                        movementId,
                        accountId,
                        LocalDateTime.of(2024, 1, 15, 10, 30),
                        MovementType.CREDITO,
                        BigDecimal.valueOf(500.00),
                        BigDecimal.valueOf(1500.00),
                        LocalDateTime.of(2024, 1, 15, 10, 31));

        savedMovement = Movement.builder().id(UUID.randomUUID()).build();

        savedMovementEntity =
                new MovementEntity(
                        savedMovement.getId(),
                        accountId,
                        LocalDateTime.of(2024, 1, 15, 10, 30),
                        MovementType.CREDITO,
                        BigDecimal.valueOf(500.00),
                        BigDecimal.valueOf(1500.00),
                        LocalDateTime.of(2024, 1, 15, 10, 31));
    }

    @Test
    void givenValidMovementWhenSaveMovementThenReturnSavedMovementMono() {
        // Arrange
        doReturn(testMovementEntity).when(movementMapper).toEntity(testMovement);
        doReturn(Mono.just(savedMovementEntity)).when(movementRepository).save(testMovementEntity);
        doReturn(savedMovement).when(movementMapper).toDomain(savedMovementEntity);

        // Act
        Mono<Movement> result = movementTransactionService.saveMovement(testMovement);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        movement -> {
                            assertAll(
                                    () -> assertNotNull(movement),
                                    () -> assertEquals(savedMovement.getId(), movement.getId()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getAccountId(),
                                                    movement.getAccountId()),
                                    () -> assertEquals(savedMovement.getDate(), movement.getDate()),
                                    () -> assertEquals(savedMovement.getType(), movement.getType()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getValue(), movement.getValue()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getPostMovementBalance(),
                                                    movement.getPostMovementBalance()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getCreatedAt(),
                                                    movement.getCreatedAt()));
                        })
                .verifyComplete();

        verify(movementMapper).toEntity(testMovement);
        verify(movementRepository).save(testMovementEntity);
        verify(movementMapper).toDomain(savedMovementEntity);
        verifyNoMoreInteractions(movementMapper, movementRepository);
    }
}
