package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementEntityTest {

    private MovementEntity movementEntity;
    private UUID testId;
    private UUID testAccountId;
    private LocalDateTime testDate;
    private MovementType testType;
    private BigDecimal testValue;
    private BigDecimal testPostMovementBalance;
    private LocalDateTime testCreatedAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testAccountId = UUID.randomUUID();
        testDate = LocalDateTime.of(2024, 1, 15, 10, 30);
        testType = MovementType.CREDITO;
        testValue = BigDecimal.valueOf(500.00);
        testPostMovementBalance = BigDecimal.valueOf(1500.00);
        testCreatedAt = LocalDateTime.of(2024, 1, 15, 10, 31);

        movementEntity = new MovementEntity();
    }

    @Test
    void givenAllArgsConstructorWhenCreateMovementEntityThenAllFieldsAreSet() {
        // Arrange & Act
        MovementEntity entity =
                new MovementEntity(
                        testId,
                        testAccountId,
                        testDate,
                        testType,
                        testValue,
                        testPostMovementBalance,
                        testCreatedAt);

        // Assert
        assertAll(
                () -> assertEquals(testId, entity.getId()),
                () -> assertEquals(testAccountId, entity.getAccountId()),
                () -> assertEquals(testDate, entity.getDate()),
                () -> assertEquals(testType, entity.getType()),
                () -> assertEquals(testValue, entity.getValue()),
                () -> assertEquals(testPostMovementBalance, entity.getPostMovementBalance()),
                () -> assertEquals(testCreatedAt, entity.getCreatedAt()));
    }

    @Test
    void givenMovementEntityWhenSetIdThenIdIsUpdated() {
        // Arrange
        UUID newId = UUID.randomUUID();

        // Act
        movementEntity.setId(newId);
        movementEntity.setAccountId(testAccountId);
        movementEntity.setDate(testDate);
        movementEntity.setType(testType);
        movementEntity.setValue(testValue);
        movementEntity.setPostMovementBalance(testPostMovementBalance);
        movementEntity.setCreatedAt(testCreatedAt);

        // Assert
        assertEquals(newId, movementEntity.getId());
        assertEquals(testAccountId, movementEntity.getAccountId());
        assertEquals(testDate, movementEntity.getDate());
        assertEquals(testType, movementEntity.getType());
        assertEquals(testValue, movementEntity.getValue());
        assertEquals(testPostMovementBalance, movementEntity.getPostMovementBalance());
        assertEquals(testCreatedAt, movementEntity.getCreatedAt());
    }
}
