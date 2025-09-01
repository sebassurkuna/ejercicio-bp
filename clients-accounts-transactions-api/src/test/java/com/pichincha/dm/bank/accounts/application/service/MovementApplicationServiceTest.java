package com.pichincha.dm.bank.accounts.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.application.command.DeleteMovementCommand;
import com.pichincha.dm.bank.accounts.application.command.UpdateMovementCommand;
import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.domain.exception.DataModifyException;
import com.pichincha.dm.bank.accounts.domain.exception.TransactionNotAllowedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@ExtendWith(MockitoExtension.class)
class MovementApplicationServiceTest {

    @Mock private MovementOutputPort movementOutputPort;

    @Mock private AccountOutputPort accountOutputPort;

    @Mock private UpdateMovementCommand updateMovementCommand;

    @Mock private DeleteMovementCommand deleteMovementCommand;

    @InjectMocks private MovementApplicationService movementApplicationService;

    private Account testAccount;
    private Movement testMovement;
    private UUID testMovementId;
    private UUID testAccountId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testMovementId = UUID.randomUUID();
        testAccount = createTestAccount(testAccountId, BigDecimal.valueOf(1000));
        testMovement =
                createTestMovement(
                        testMovementId,
                        testAccountId,
                        MovementType.CREDITO,
                        BigDecimal.valueOf(500));
    }

    @Test
    void givenValidCreditMovementWhenCreateMovementThenReturnMovementWithUpdatedBalance() {
        // Arrange
        Movement creditMovement =
                createTestMovement(
                        null, testAccountId, MovementType.CREDITO, BigDecimal.valueOf(500));
        Movement expectedMovement =
                createTestMovement(
                        testMovementId,
                        testAccountId,
                        MovementType.CREDITO,
                        BigDecimal.valueOf(500));
        expectedMovement.setPostMovementBalance(BigDecimal.valueOf(1500));

        doReturn(Mono.just(testAccount)).when(accountOutputPort).findById(testAccountId);

        doReturn(Mono.just(testAccount))
                .when(accountOutputPort)
                .updateBalance(eq(testAccountId), eq(BigDecimal.valueOf(1500)));

        doReturn(Mono.just(expectedMovement)).when(movementOutputPort).save(any(Movement.class));

        // Act
        Mono<Movement> result = movementApplicationService.createMovement(creditMovement);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(
                        movement ->
                                movement.getPostMovementBalance().equals(BigDecimal.valueOf(1500))
                                        && movement.getType() == MovementType.CREDITO
                                        && movement.getValue().equals(BigDecimal.valueOf(500)))
                .verifyComplete();

        verify(accountOutputPort).updateBalance(testAccountId, BigDecimal.valueOf(1500));
        verify(movementOutputPort).save(any(Movement.class));
    }

    @Test
    void
            givenCreditMovementWithNegativeValueWhenCreateMovementThenThrowTransactionNotAllowedException() {
        // Arrange
        Movement invalidCreditMovement =
                createTestMovement(
                        null, testAccountId, MovementType.CREDITO, BigDecimal.valueOf(-100));

        doReturn(Mono.just(testAccount)).when(accountOutputPort).findById(testAccountId);

        // Act
        Mono<Movement> result = movementApplicationService.createMovement(invalidCreditMovement);

        // Assert
        StepVerifier.create(result).expectError(TransactionNotAllowedException.class).verify();
    }

    @Test
    void
            givenDebitMovementWithPositiveValueWhenCreateMovementThenThrowTransactionNotAllowedException() {
        // Arrange
        Movement invalidDebitMovement =
                createTestMovement(
                        null, testAccountId, MovementType.DEBITO, BigDecimal.valueOf(100));

        doReturn(Mono.just(testAccount)).when(accountOutputPort).findById(testAccountId);

        // Act
        Mono<Movement> result = movementApplicationService.createMovement(invalidDebitMovement);

        // Assert
        StepVerifier.create(result).expectError(TransactionNotAllowedException.class).verify();
    }

    @Test
    void givenAccountNotFoundWhenCreateMovementThenReturnDataModifyException() {
        // Arrange
        RuntimeException repositoryException = new RuntimeException("Account not found");

        doReturn(Mono.error(repositoryException)).when(accountOutputPort).findById(testAccountId);

        // Act
        Mono<Movement> result = movementApplicationService.createMovement(testMovement);

        // Assert
        StepVerifier.create(result).expectError(DataModifyException.class).verify();
    }

    @Test
    void givenValidMovementIdWhenGetMovementByIdThenReturnMovement() {
        // Arrange
        doReturn(Mono.just(testMovement)).when(movementOutputPort).findById(testMovementId);

        // Act
        Mono<Movement> result = movementApplicationService.getMovementById(testMovementId);

        // Assert
        StepVerifier.create(result).expectNext(testMovement).verifyComplete();
    }

    @Test
    void givenValidParametersWhenListMovementsThenReturnMovements() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Long accountNumber = 123456789L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        Integer page = 0;
        Integer size = 10;

        Movement movement1 =
                createTestMovement(
                        UUID.randomUUID(),
                        testAccountId,
                        MovementType.CREDITO,
                        BigDecimal.valueOf(300));
        Movement movement2 =
                createTestMovement(
                        UUID.randomUUID(),
                        testAccountId,
                        MovementType.DEBITO,
                        BigDecimal.valueOf(-150));
        Flux<Movement> expectedMovements = Flux.just(movement1, movement2);

        doReturn(expectedMovements)
                .when(movementOutputPort)
                .findAll(clientId, accountNumber, startDate, endDate, page, size);

        // Act
        Flux<Movement> result =
                movementApplicationService.listMovements(
                        clientId, accountNumber, startDate, endDate, page, size);

        // Assert
        StepVerifier.create(result).expectNext(movement1).expectNext(movement2).verifyComplete();
    }

    @Test
    void givenValidMovementUpdateWhenUpdateMovementThenReturnUpdatedMovement() {
        // Arrange
        Movement existingMovement =
                createTestMovement(
                        testMovementId,
                        testAccountId,
                        MovementType.CREDITO,
                        BigDecimal.valueOf(300));
        Movement updatedMovement =
                createTestMovement(
                        testMovementId,
                        testAccountId,
                        MovementType.CREDITO,
                        BigDecimal.valueOf(400));

        Tuple2<Account, Movement> tuple = Tuples.of(testAccount, existingMovement);

        doReturn(Mono.just(testAccount)).when(accountOutputPort).findById(testAccountId);

        doReturn(Mono.just(existingMovement)).when(movementOutputPort).findById(testMovementId);

        doReturn(updateMovementCommand)
                .when(updateMovementCommand)
                .withNewMovement(updatedMovement);

        doReturn(updateMovementCommand)
                .when(updateMovementCommand)
                .withOldMovement(existingMovement);

        doReturn(updateMovementCommand).when(updateMovementCommand).withAccount(testAccount);

        doReturn(Mono.empty()).when(updateMovementCommand).execute();

        // Act
        Mono<Movement> result = movementApplicationService.updateMovement(updatedMovement);

        // Assert
        StepVerifier.create(result).expectNext(updatedMovement).verifyComplete();

        verify(updateMovementCommand).withNewMovement(updatedMovement);
        verify(updateMovementCommand).withOldMovement(existingMovement);
        verify(updateMovementCommand).withAccount(testAccount);
        verify(updateMovementCommand).execute();
    }

    @Test
    void givenValidMovementIdWhenDeleteMovementThenCompleteSuccessfully() {
        // Arrange
        doReturn(Mono.just(testMovement)).when(movementOutputPort).findById(testMovementId);

        doReturn(Mono.just(testAccount)).when(accountOutputPort).findById(testAccountId);

        doReturn(deleteMovementCommand)
                .when(deleteMovementCommand)
                .withMovementToDelete(testMovement);

        doReturn(deleteMovementCommand).when(deleteMovementCommand).withAccount(testAccount);

        doReturn(Mono.empty()).when(deleteMovementCommand).execute();

        // Act
        Mono<Void> result = movementApplicationService.deleteMovement(testMovementId);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(deleteMovementCommand).withMovementToDelete(testMovement);
        verify(deleteMovementCommand).withAccount(testAccount);
        verify(deleteMovementCommand).execute();
    }

    private Account createTestAccount(UUID id, BigDecimal balance) {
        return Account.builder()
                .id(id)
                .clientId(UUID.randomUUID())
                .accountNumber(Long.valueOf("123456789"))
                .type(AccountType.AHORROS)
                .initBalance(BigDecimal.valueOf(1000))
                .currentBalance(balance)
                .state(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Movement createTestMovement(
            UUID id, UUID accountId, MovementType type, BigDecimal value) {
        return Movement.builder()
                .id(id)
                .accountId(accountId)
                .date(LocalDateTime.now())
                .type(type)
                .value(value)
                .postMovementBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
