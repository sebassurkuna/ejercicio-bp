package com.pichincha.dm.bank.accounts.application.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pichincha.dm.bank.accounts.application.validation.impl.ZeroBalanceValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementValidationChainTest {

    @Mock private MovementValidationStrategy firstValidation;

    @Mock private MovementValidationStrategy secondValidation;

    @Mock private MovementValidationStrategy thirdValidation;

    @BeforeEach
    void setUp() {}

    @Test
    void givenDefaultChainWhenCreateDefaultChainThenReturnChainWithCorrectValidations() {
        // Arrange & Act
        MovementValidationChain chain = MovementValidationChain.createDefaultChain();

        // Assert
        assertNotNull(chain);
        assertNotNull(chain.getValidationChain());
        assertTrue(chain.getValidationChain() instanceof ZeroBalanceValidation);
    }

    @Test
    void givenBuilderWhenBuilderThenReturnNewBuilderInstance() {
        // Arrange & Act
        MovementValidationChain.Builder builder = MovementValidationChain.builder();

        // Assert
        assertNotNull(builder);
    }

    @Test
    void givenSingleValidationWhenBuildChainThenReturnChainWithSingleValidation() {
        // Arrange
        MovementValidationChain.Builder builder = MovementValidationChain.builder();

        // Act
        MovementValidationChain chain = builder.addValidation(firstValidation).build();

        // Assert
        assertNotNull(chain);
        assertNotNull(chain.getValidationChain());
        assertEquals(firstValidation, chain.getValidationChain());
    }

    @Test
    void givenMultipleValidationsWhenBuildChainThenReturnChainWithCorrectOrder() {
        // Arrange
        MovementValidationChain.Builder builder = MovementValidationChain.builder();

        // Act
        MovementValidationChain chain =
                builder.addValidation(firstValidation)
                        .addValidation(secondValidation)
                        .addValidation(thirdValidation)
                        .build();

        // Assert
        assertNotNull(chain);
        assertNotNull(chain.getValidationChain());
        assertEquals(firstValidation, chain.getValidationChain());
    }
}
