package com.pichincha.dm.bank.accounts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class DataAccessExceptionTest {

    @Test
    void givenThrowableWhenCreateDataAccessExceptionThenExceptionIsCreated() {
        // Arrange
        String originalMessage = "Database connection failed";
        Throwable cause =
                new RuntimeException(originalMessage, new RuntimeException(originalMessage));

        // Act
        DataAccessException exception = new DataAccessException(cause);

        // Assert
        assertNotNull(exception);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals("Data access error occurred", exception.getBusinessMessage());
        assertEquals(HttpStatusCode.valueOf(400), exception.getStatusCode());
    }
}
