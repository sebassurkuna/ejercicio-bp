package com.pichincha.dm.bank.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementTest {

    private UUID movementId;
    private UUID accountId;
    private LocalDateTime date;
    private BigDecimal value;
    private BigDecimal postMovementBalance;
    private LocalDateTime createdAt;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        movementId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        date = LocalDateTime.now().minusHours(1);
        value = new BigDecimal("250.75");
        postMovementBalance = new BigDecimal("1250.75");
        createdAt = LocalDateTime.now();
        mockAccount =
                Account.builder()
                        .id(accountId)
                        .accountNumber(123456789L)
                        .currentBalance(new BigDecimal("1000.00"))
                        .build();
    }

    @Test
    void givenValidDataWhenBuildMovementThenMovementIsCreated() {
        // Arrange & Act
        Movement movement =
                Movement.builder()
                        .id(movementId)
                        .accountId(accountId)
                        .date(date)
                        .type(MovementType.CREDITO)
                        .value(value)
                        .postMovementBalance(postMovementBalance)
                        .createdAt(createdAt)
                        .account(mockAccount)
                        .build();

        // Assert
        assertNotNull(movement);
        assertEquals(movementId, movement.getId());
        assertEquals(accountId, movement.getAccountId());
        assertEquals(date, movement.getDate());
        assertEquals(MovementType.CREDITO, movement.getType());
        assertEquals(value, movement.getValue());
        assertEquals(postMovementBalance, movement.getPostMovementBalance());
        assertEquals(createdAt, movement.getCreatedAt());
        assertEquals(mockAccount, movement.getAccount());
    }

    @Test
    void givenMovementWhenSetIdThenIdIsUpdated() {
        // Arrange
        Movement movement = Movement.builder().build();
        UUID newId = UUID.randomUUID();

        // Act
        movement.setId(newId);
        movement.setAccountId(newId);
        movement.setDate(date);
        movement.setType(MovementType.DEBITO);
        movement.setValue(value);
        movement.setPostMovementBalance(postMovementBalance);
        movement.setCreatedAt(createdAt);
        movement.setAccount(mockAccount);

        // Assert
        assertEquals(newId, movement.getId());
    }
}
