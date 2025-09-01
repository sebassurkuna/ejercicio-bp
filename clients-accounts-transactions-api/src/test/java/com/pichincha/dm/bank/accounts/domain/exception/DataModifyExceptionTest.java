package com.pichincha.dm.bank.accounts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class DataModifyExceptionTest {

    @Test
    void givenThrowableWhenCreateDataModifyExceptionThenExceptionIsCreated() {
        // Arrange
        String originalMessage = "Database update failed";
        Throwable cause =
                new RuntimeException(originalMessage, new RuntimeException(originalMessage));

        // Act
        DataModifyException exception = new DataModifyException(cause);

        // Assert
        assertNotNull(exception);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals("Data modification error occurred", exception.getBusinessMessage());
        assertEquals(HttpStatusCode.valueOf(400), exception.getStatusCode());
    }
}
