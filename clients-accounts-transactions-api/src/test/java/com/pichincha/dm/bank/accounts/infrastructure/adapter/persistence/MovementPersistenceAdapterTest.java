package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.MovementEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.MovementRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.MovementTransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
class MovementPersistenceAdapterTest {

    @Mock private MovementRepository movementRepository;

    @Mock private MovementEntityMapper movementMapper;

    @Mock private MovementTransactionService transactionService;

    @InjectMocks private MovementPersistenceAdapter movementPersistenceAdapter;

    private Movement testMovement;
    private MovementEntity testMovementEntity;
    private UUID testMovementId;
    private UUID testAccountId;
    private UUID testClientId;

    @BeforeEach
    void setUp() {
        testMovementId = UUID.randomUUID();
        testAccountId = UUID.randomUUID();
        testClientId = UUID.randomUUID();

        testMovement =
                Movement.builder()
                        .id(testMovementId)
                        .accountId(testAccountId)
                        .type(MovementType.CREDITO)
                        .value(BigDecimal.valueOf(500.00))
                        .postMovementBalance(BigDecimal.valueOf(1500.00))
                        .date(LocalDateTime.of(2024, 1, 15, 10, 30))
                        .build();

        testMovementEntity =
                new MovementEntity(
                        testMovementId,
                        testAccountId,
                        LocalDateTime.of(2024, 1, 15, 10, 30),
                        MovementType.CREDITO,
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(500.00),
                        LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    @Test
    void givenValidMovementWhenSaveThenReturnSavedMovementMono() {
        // Arrange
        Movement savedMovement = Movement.builder().id(UUID.randomUUID()).build();

        doReturn(Mono.just(savedMovement)).when(transactionService).saveMovement(testMovement);
        doReturn(Mono.just(testMovementEntity))
                .when(movementRepository)
                .findById(savedMovement.getId());
        doReturn(savedMovement).when(movementMapper).toDomain(testMovementEntity);

        // Act
        Mono<Movement> result = movementPersistenceAdapter.save(testMovement);

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
                                    () -> assertEquals(savedMovement.getType(), movement.getType()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getValue(), movement.getValue()),
                                    () ->
                                            assertEquals(
                                                    savedMovement.getPostMovementBalance(),
                                                    movement.getPostMovementBalance()));
                        })
                .verifyComplete();

        verify(transactionService).saveMovement(testMovement);
        verify(movementRepository).findById(savedMovement.getId());
        verify(movementMapper).toDomain(testMovementEntity);
    }

    @Test
    void givenValidFiltersWhenFindAllThenReturnMovementsFlux() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long accountNumber = 1001L;
        Integer page = 0;
        Integer size = 10;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);
        int offset = 0;

        Movement secondMovement =
                Movement.builder()
                        .id(UUID.randomUUID())
                        .type(MovementType.DEBITO)
                        .value(BigDecimal.valueOf(200.00))
                        .postMovementBalance(BigDecimal.valueOf(1300.00))
                        .build();

        MovementEntity secondMovementEntity =
                new MovementEntity(
                        secondMovement.getId(),
                        testAccountId,
                        LocalDateTime.of(2024, 1, 16, 14, 45),
                        MovementType.DEBITO,
                        BigDecimal.valueOf(1500.00),
                        BigDecimal.valueOf(200.00),
                        LocalDateTime.of(2024, 1, 16, 14, 45));

        Flux<MovementEntity> movementEntities = Flux.just(testMovementEntity, secondMovementEntity);

        doReturn(movementEntities)
                .when(movementRepository)
                .findWithFilters(
                        testClientId, accountNumber, startDateTime, endDateTime, size, offset);
        doReturn(testMovement).when(movementMapper).toDomain(testMovementEntity);
        doReturn(secondMovement).when(movementMapper).toDomain(secondMovementEntity);

        // Act
        Flux<Movement> result =
                movementPersistenceAdapter.findAll(
                        testClientId, accountNumber, startDate, endDate, page, size);

        // Assert
        StepVerifier.create(result)
                .assertNext(movement -> assertEquals(testMovement.getId(), movement.getId()))
                .assertNext(movement -> assertEquals(secondMovement.getId(), movement.getId()))
                .verifyComplete();

        verify(movementRepository)
                .findWithFilters(
                        testClientId, accountNumber, startDateTime, endDateTime, size, offset);
        verify(movementMapper).toDomain(testMovementEntity);
        verify(movementMapper).toDomain(secondMovementEntity);
    }

    @Test
    void givenValidMovementWhenUpdateThenReturnUpdatedMovementMono() {
        // Arrange
        Movement movementToUpdate =
                Movement.builder()
                        .value(BigDecimal.valueOf(300.00))
                        .postMovementBalance(BigDecimal.valueOf(1300.00))
                        .build();

        MovementEntity updatedEntity =
                new MovementEntity(
                        testMovementId,
                        testAccountId,
                        testMovement.getDate(),
                        testMovement.getType(),
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(300.00),
                        testMovement.getCreatedAt());

        doReturn(updatedEntity).when(movementMapper).toEntity(movementToUpdate);
        doReturn(Mono.just(updatedEntity)).when(movementRepository).save(updatedEntity);
        doReturn(movementToUpdate).when(movementMapper).toDomain(updatedEntity);

        // Act
        Mono<Movement> result = movementPersistenceAdapter.update(movementToUpdate);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        movement -> {
                            assertAll(
                                    () -> assertNotNull(movement),
                                    () ->
                                            assertEquals(
                                                    movementToUpdate.getValue(),
                                                    movement.getValue()),
                                    () ->
                                            assertEquals(
                                                    movementToUpdate.getPostMovementBalance(),
                                                    movement.getPostMovementBalance()));
                        })
                .verifyComplete();

        verify(movementMapper).toEntity(movementToUpdate);
        verify(movementRepository).save(updatedEntity);
        verify(movementMapper).toDomain(updatedEntity);
    }

    @Test
    void givenValidIdWhenExistsByIdThenReturnTrueMono() {
        // Arrange
        doReturn(Mono.just(true)).when(movementRepository).existsById(testMovementId);

        // Act
        Mono<Boolean> result = movementPersistenceAdapter.existsById(testMovementId);

        // Assert
        StepVerifier.create(result).assertNext(exists -> assertTrue(exists)).verifyComplete();

        verify(movementRepository).existsById(testMovementId);
    }

    @Test
    void givenValidIdWhenDeleteByIdThenReturnMonoVoid() {
        // Arrange
        doReturn(Mono.empty()).when(movementRepository).deleteById(testMovementId);

        // Act
        Mono<Void> result = movementPersistenceAdapter.deleteById(testMovementId);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(movementRepository).deleteById(testMovementId);
    }

    @Test
    void givenValidMovementsListWhenUpdateBatchThenReturnUpdatedMovementsFlux() {
        // Arrange
        Movement secondMovement =
                Movement.builder().id(UUID.randomUUID()).value(BigDecimal.valueOf(200.00)).build();

        List<Movement> movements = Arrays.asList(testMovement, secondMovement);

        MovementEntity secondEntity =
                new MovementEntity(
                        testMovementId,
                        testAccountId,
                        secondMovement.getDate(),
                        MovementType.CREDITO,
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(500.00),
                        LocalDateTime.of(2024, 1, 16, 10, 0));

        doReturn(testMovementEntity).when(movementMapper).toEntity(testMovement);
        doReturn(secondEntity).when(movementMapper).toEntity(secondMovement);

        doReturn(Mono.just(testMovementEntity)).when(movementRepository).save(testMovementEntity);
        doReturn(Mono.just(secondEntity)).when(movementRepository).save(secondEntity);

        doReturn(testMovement).when(movementMapper).toDomain(testMovementEntity);
        doReturn(secondMovement).when(movementMapper).toDomain(secondEntity);

        // Act
        Flux<Movement> result = movementPersistenceAdapter.updateBatch(movements);

        // Assert
        StepVerifier.create(result)
                .assertNext(movement -> assertEquals(testMovement.getId(), movement.getId()))
                .assertNext(movement -> assertEquals(secondMovement.getId(), movement.getId()))
                .verifyComplete();

        verify(movementMapper).toEntity(testMovement);
        verify(movementMapper).toEntity(secondMovement);
        verify(movementRepository).save(testMovementEntity);
        verify(movementRepository).save(secondEntity);
        verify(movementMapper).toDomain(testMovementEntity);
        verify(movementMapper).toDomain(secondEntity);
    }
}
