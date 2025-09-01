package com.pichincha.dm.bank.accounts.application.validation.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.domain.exception.TransactionNotAllowedException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZeroBalanceValidationTest {

    private ZeroBalanceValidation zeroBalanceValidation;

    @BeforeEach
    void setUp() {
        zeroBalanceValidation = new ZeroBalanceValidation();
    }

    @Test
    void givenValidDebitMovementWhenValidateThenValidationPasses() {
        // Arrange
        Account accountWithBalance =
                Account.builder()
                        .id(UUID.randomUUID())
                        .clientId(UUID.randomUUID())
                        .accountNumber(123456L)
                        .type(AccountType.AHORROS)
                        .initBalance(new BigDecimal("1000.00"))
                        .currentBalance(new BigDecimal("1000.00"))
                        .build();

        Movement validDebitMovement =
                Movement.builder()
                        .id(UUID.randomUUID())
                        .accountId(accountWithBalance.getId())
                        .date(LocalDateTime.now())
                        .type(MovementType.DEBITO)
                        .value(new BigDecimal("-500.00"))
                        .postMovementBalance(new BigDecimal("500.00"))
                        .build();

        // Act & Assert
        assertDoesNotThrow(
                () -> zeroBalanceValidation.validate(accountWithBalance, validDebitMovement));
    }

    @Test
    void givenInvalidDebitMovementWhenValidateThenThrowException() {
        // Arrange
        Account accountWithLimitedBalance =
                Account.builder()
                        .id(UUID.randomUUID())
                        .clientId(UUID.randomUUID())
                        .accountNumber(123456L)
                        .type(AccountType.AHORROS)
                        .initBalance(new BigDecimal("1000.00"))
                        .currentBalance(new BigDecimal("100.00"))
                        .build();

        Movement invalidDebitMovement =
                Movement.builder()
                        .id(UUID.randomUUID())
                        .accountId(accountWithLimitedBalance.getId())
                        .date(LocalDateTime.now())
                        .type(MovementType.DEBITO)
                        .value(new BigDecimal("-200.00"))
                        .postMovementBalance(new BigDecimal("-100.00"))
                        .build();

        // Act & Assert
        TransactionNotAllowedException exception =
                assertThrows(
                        TransactionNotAllowedException.class,
                        () ->
                                zeroBalanceValidation.validate(
                                        accountWithLimitedBalance, invalidDebitMovement));

        String expectedMessage =
                "Fondos insuficientes. Saldo actual: 100.00, Monto solicitado: 200.00";
        assertEquals(expectedMessage, exception.getBusinessMessage());
    }
}
