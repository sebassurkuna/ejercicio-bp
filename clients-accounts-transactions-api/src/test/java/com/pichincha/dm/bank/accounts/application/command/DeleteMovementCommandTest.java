package com.pichincha.dm.bank.accounts.application.command;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
class DeleteMovementCommandTest {

    @Mock private MovementOutputPort movementOutputPort;

    @Mock private AccountOutputPort accountOutputPort;

    @Mock private BalanceRecalculationService balanceRecalculationService;

    @InjectMocks private DeleteMovementCommand deleteMovementCommand;

    private Account testAccount;
    private Movement testMovement;
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

        testMovement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(movementDate)
                        .type(MovementType.DEBITO)
                        .value(valueOf(-200))
                        .postMovementBalance(valueOf(800))
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void shouldReturnSameInstanceWhenWithMovementToDeleteCalled() {
        // Arrange
        Movement movement = createTestMovement();

        // Act
        DeleteMovementCommand result = deleteMovementCommand.withMovementToDelete(movement);

        // Assert
        assertSame(deleteMovementCommand, result);
    }

    @Test
    void shouldReturnSameInstanceWhenWithAccountCalled() {
        // Arrange
        Account account = createTestAccount();

        // Act
        DeleteMovementCommand result = deleteMovementCommand.withAccount(account);

        // Assert
        assertSame(deleteMovementCommand, result);
    }

    @Test
    void givenValidMovementAndAccountWhenExecuteThenDeleteMovementSuccessfully() {
        // Arrange
        deleteMovementCommand.withMovementToDelete(testMovement).withAccount(testAccount);

        BigDecimal expectedNewBalance = valueOf(1000);
        BigDecimal expectedBalanceAdjustment = valueOf(200);

        doReturn(Mono.just(testAccount))
                .when(accountOutputPort)
                .updateBalance(accountId, expectedNewBalance);

        doReturn(Mono.empty()).when(movementOutputPort).deleteById(movementId);

        doReturn(Mono.empty())
                .when(balanceRecalculationService)
                .recalculatePostMovementBalances(
                        eq(accountId),
                        eq(movementDate.plusNanos(1)),
                        eq(expectedBalanceAdjustment));

        // Act
        Mono<Void> result = deleteMovementCommand.execute();

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(accountOutputPort).updateBalance(accountId, expectedNewBalance);
        verify(movementOutputPort).deleteById(movementId);
        verify(balanceRecalculationService)
                .recalculatePostMovementBalances(
                        accountId, movementDate.plusNanos(1), expectedBalanceAdjustment);
    }

    private Movement createTestMovement() {
        return Movement.builder()
                .id(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .date(LocalDateTime.now())
                .type(MovementType.DEBITO)
                .value(valueOf(-100))
                .postMovementBalance(valueOf(900))
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
