package com.pichincha.dm.bank.accounts.application.command;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.application.service.BalanceRecalculationService;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
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
class UpdateMovementCommandTest {

    @Mock private MovementOutputPort movementOutputPort;

    @Mock private AccountOutputPort accountOutputPort;

    @Mock private BalanceRecalculationService balanceRecalculationService;

    @InjectMocks private UpdateMovementCommand updateMovementCommand;

    private Account testAccount;
    private Movement oldMovement;
    private Movement newMovement;
    private UUID accountId;
    private UUID movementId;
    private LocalDateTime movementDate;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        movementId = UUID.randomUUID();
        movementDate = LocalDateTime.now();

        testAccount =
                Account.builder()
                        .id(accountId)
                        .clientId(UUID.randomUUID())
                        .accountNumber(1234567890L)
                        .type(AccountType.AHORROS)
                        .initBalance(valueOf(1000))
                        .currentBalance(valueOf(800))
                        .state(true)
                        .createdAt(LocalDateTime.now())
                        .build();

        oldMovement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(movementDate)
                        .type(MovementType.DEBITO)
                        .value(valueOf(-200))
                        .postMovementBalance(valueOf(800))
                        .createdAt(LocalDateTime.now())
                        .build();

        newMovement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(movementDate)
                        .type(MovementType.DEBITO)
                        .value(valueOf(-150))
                        .postMovementBalance(valueOf(850))
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void shouldReturnSameInstanceWhenWithOldMovementCalled() {
        // Arrange
        Movement movement = createTestMovement();

        // Act
        UpdateMovementCommand result = updateMovementCommand.withOldMovement(movement);

        // Assert
        assertSame(updateMovementCommand, result);
    }

    @Test
    void shouldReturnSameInstanceWhenWithNewMovementCalled() {
        // Arrange
        Movement movement = createTestMovement();

        // Act
        UpdateMovementCommand result = updateMovementCommand.withNewMovement(movement);

        // Assert
        assertSame(updateMovementCommand, result);
    }

    @Test
    void shouldReturnSameInstanceWhenWithAccountCalled() {
        // Arrange
        Account account = createTestAccount();

        // Act
        UpdateMovementCommand result = updateMovementCommand.withAccount(account);

        // Assert
        assertSame(updateMovementCommand, result);
    }

    @Test
    void givenValidMovementsWhenExecuteThenUpdateMovementSuccessfully() {
        // Arrange
        updateMovementCommand
                .withOldMovement(oldMovement)
                .withNewMovement(newMovement)
                .withAccount(testAccount);

        BigDecimal expectedBalanceAdjustment = valueOf(50);
        BigDecimal expectedNewAccountBalance = valueOf(850);
        BigDecimal expectedPostMovementBalance = valueOf(850);

        doReturn(Mono.just(testAccount))
                .when(accountOutputPort)
                .updateBalance(accountId, expectedNewAccountBalance);

        doReturn(Mono.just(newMovement)).when(movementOutputPort).update(any(Movement.class));

        doReturn(Mono.empty())
                .when(balanceRecalculationService)
                .recalculatePostMovementBalances(
                        eq(accountId),
                        eq(movementDate.plusNanos(1)),
                        eq(expectedBalanceAdjustment));

        // Act
        Mono<Void> result = updateMovementCommand.execute();

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(accountOutputPort).updateBalance(accountId, expectedNewAccountBalance);
        verify(movementOutputPort).update(any(Movement.class));
        verify(balanceRecalculationService)
                .recalculatePostMovementBalances(
                        accountId, movementDate.plusNanos(1), expectedBalanceAdjustment);

        assertEquals(expectedPostMovementBalance, newMovement.getPostMovementBalance());
    }

    @Test
    void givenNoBalanceChangeWhenExecuteThenSkipRecalculation() {
        // Arrange
        Movement sameValueMovement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(movementDate)
                        .type(MovementType.DEBITO)
                        .value(valueOf(-200))
                        .postMovementBalance(valueOf(800))
                        .createdAt(LocalDateTime.now())
                        .build();

        updateMovementCommand
                .withOldMovement(oldMovement)
                .withNewMovement(sameValueMovement)
                .withAccount(testAccount);

        BigDecimal expectedBalanceAdjustment = valueOf(0);
        BigDecimal expectedNewAccountBalance = valueOf(800);

        doReturn(Mono.just(testAccount))
                .when(accountOutputPort)
                .updateBalance(accountId, expectedNewAccountBalance);

        doReturn(Mono.just(sameValueMovement)).when(movementOutputPort).update(any(Movement.class));

        // Act
        Mono<Void> result = updateMovementCommand.execute();

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(accountOutputPort).updateBalance(accountId, expectedNewAccountBalance);
        verify(movementOutputPort).update(any(Movement.class));
        verifyNoInteractions(balanceRecalculationService);
    }

    private Movement createTestMovement() {
        return Movement.builder()
                .id(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .date(LocalDateTime.now())
                .type(MovementType.CREDITO)
                .value(valueOf(100))
                .postMovementBalance(valueOf(1100))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Account createTestAccount() {
        return Account.builder()
                .id(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .accountNumber(9876543210L)
                .type(AccountType.CORRIENTE)
                .initBalance(valueOf(2000))
                .currentBalance(valueOf(1500))
                .state(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
