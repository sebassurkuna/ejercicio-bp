package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountEntityTest {

    private AccountEntity accountEntity;
    private UUID testId;
    private UUID testClientId;
    private Long testAccountNumber;
    private AccountType testType;
    private BigDecimal testInitBalance;
    private BigDecimal testCurrentBalance;
    private Boolean testState;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testClientId = UUID.randomUUID();
        testAccountNumber = 1001L;
        testType = AccountType.AHORROS;
        testInitBalance = BigDecimal.valueOf(1000.00);
        testCurrentBalance = BigDecimal.valueOf(1500.00);
        testState = true;
        testCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        testUpdatedAt = LocalDateTime.of(2024, 1, 15, 15, 30);

        accountEntity = new AccountEntity();
    }

    @Test
    void givenAllArgsConstructorWhenCreateAccountEntityThenAllFieldsAreSet() {
        // Arrange & Act
        AccountEntity entity =
                new AccountEntity(
                        testId,
                        testClientId,
                        testAccountNumber,
                        testType,
                        testInitBalance,
                        testCurrentBalance,
                        testState,
                        testCreatedAt,
                        testUpdatedAt);

        // Assert
        assertAll(
                () -> assertEquals(testId, entity.getId()),
                () -> assertEquals(testClientId, entity.getClientId()),
                () -> assertEquals(testAccountNumber, entity.getAccountNumber()),
                () -> assertEquals(testType, entity.getType()),
                () -> assertEquals(testInitBalance, entity.getInitBalance()),
                () -> assertEquals(testCurrentBalance, entity.getCurrentBalance()),
                () -> assertEquals(testState, entity.getState()),
                () -> assertEquals(testCreatedAt, entity.getCreatedAt()),
                () -> assertEquals(testUpdatedAt, entity.getUpdatedAt()));
    }

    @Test
    void givenAccountEntityWhenSetIdThenIdIsUpdated() {
        // Arrange
        UUID newId = UUID.randomUUID();

        // Act
        accountEntity.setId(newId);
        accountEntity.setClientId(testClientId);
        accountEntity.setAccountNumber(testAccountNumber);
        accountEntity.setType(testType);
        accountEntity.setInitBalance(testInitBalance);
        accountEntity.setCurrentBalance(testCurrentBalance);
        accountEntity.setState(testState);
        accountEntity.setCreatedAt(testCreatedAt);
        accountEntity.setUpdatedAt(testUpdatedAt);

        // Assert
        assertEquals(newId, accountEntity.getId());
        assertEquals(testClientId, accountEntity.getClientId());
        assertEquals(testAccountNumber, accountEntity.getAccountNumber());
        assertEquals(testType, accountEntity.getType());
        assertEquals(testInitBalance, accountEntity.getInitBalance());
        assertEquals(testCurrentBalance, accountEntity.getCurrentBalance());
        assertEquals(testState, accountEntity.getState());
        assertEquals(testCreatedAt, accountEntity.getCreatedAt());
        assertEquals(testUpdatedAt, accountEntity.getUpdatedAt());
    }
}
