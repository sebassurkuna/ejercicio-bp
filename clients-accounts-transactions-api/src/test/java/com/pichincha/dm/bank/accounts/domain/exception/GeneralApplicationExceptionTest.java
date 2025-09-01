package com.pichincha.dm.bank.accounts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class GeneralApplicationExceptionTest {

    @Test
    void givenValidParametersWhenCreateExceptionThenExceptionIsCreated() {
        // Arrange
        String message = "Test error message";
        String businessMessage = "Business error occurred";
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        // Act
        GeneralApplicationException exception =
                new GeneralApplicationException(message, businessMessage, statusCode);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(businessMessage, exception.getBusinessMessage());
        assertEquals(statusCode, exception.getStatusCode());
    }
}
