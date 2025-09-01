package com.pichincha.dm.bank.accounts.application.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractMovementValidationTest {

    @Mock private MovementValidationStrategy nextValidation;

    private TestMovementValidation testMovementValidation;
    private Account mockAccount;
    private Movement mockMovement;

    @BeforeEach
    void setUp() {
        testMovementValidation = new TestMovementValidation();

        mockAccount =
                Account.builder()
                        .id(UUID.randomUUID())
                        .clientId(UUID.randomUUID())
                        .accountNumber(123456L)
                        .type(AccountType.AHORROS)
                        .initBalance(new BigDecimal("1000.00"))
                        .currentBalance(new BigDecimal("1500.00"))
                        .build();

        mockMovement =
                Movement.builder()
                        .id(UUID.randomUUID())
                        .accountId(mockAccount.getId())
                        .date(LocalDateTime.now())
                        .type(MovementType.DEBITO)
                        .value(new BigDecimal("200.00"))
                        .postMovementBalance(new BigDecimal("1300.00"))
                        .build();
    }

    @Test
    void givenNextValidationWhenSetNextThenChainIsEstablished() {
        // Arrange & Act
        testMovementValidation.setNext(nextValidation);

        // Assert
        assertNotNull(testMovementValidation.getNext());
        assertEquals(nextValidation, testMovementValidation.getNext());
    }

    @Test
    void givenValidAccountAndMovementWhenValidateThenDoValidateIsCalled() {
        // Arrange
        testMovementValidation.setShouldThrowException(false);

        // Act
        assertDoesNotThrow(() -> testMovementValidation.validate(mockAccount, mockMovement));

        // Assert
        assertEquals(1, testMovementValidation.getDoValidateCallCount());
    }

    @Test
    void givenValidAccountAndMovementWithNextValidationWhenValidateThenBothValidationsAreCalled() {
        // Arrange
        testMovementValidation.setShouldThrowException(false);
        testMovementValidation.setNext(nextValidation);
        doNothing().when(nextValidation).validate(any(Account.class), any(Movement.class));

        // Act
        testMovementValidation.validate(mockAccount, mockMovement);

        // Assert
        assertEquals(1, testMovementValidation.getDoValidateCallCount());
        verify(nextValidation, times(1)).validate(mockAccount, mockMovement);
    }

    private static class TestMovementValidation extends AbstractMovementValidation {
        private boolean shouldThrowException = false;
        private int doValidateCallCount = 0;
        private Account lastValidatedAccount;
        private Movement lastValidatedMovement;
        private MovementValidationStrategy next;

        @Override
        protected void doValidate(Account account, Movement movement) {
            doValidateCallCount++;
            lastValidatedAccount = account;
            lastValidatedMovement = movement;

            if (shouldThrowException) {
                throw new TransactionNotAllowedException("Test validation failed");
            }
        }

        @Override
        public void setNext(MovementValidationStrategy next) {
            super.setNext(next);
            this.next = next;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

        public int getDoValidateCallCount() {
            return doValidateCallCount;
        }

        public MovementValidationStrategy getNext() {
            return next;
        }
    }
}
