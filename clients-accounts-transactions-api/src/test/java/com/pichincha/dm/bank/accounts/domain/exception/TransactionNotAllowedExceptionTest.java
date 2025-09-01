package com.pichincha.dm.bank.accounts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class TransactionNotAllowedExceptionTest {

    @Test
    void givenMessageWhenCreateTransactionNotAllowedExceptionThenExceptionIsCreated() {
        // Arrange
        String message = "Insufficient funds for transaction";

        // Act
        TransactionNotAllowedException exception = new TransactionNotAllowedException(message);

        // Assert
        assertNotNull(exception);
        assertEquals("Transaction not allowed", exception.getMessage());
        assertEquals(message, exception.getBusinessMessage());
        assertEquals(HttpStatusCode.valueOf(403), exception.getStatusCode());
    }
}
