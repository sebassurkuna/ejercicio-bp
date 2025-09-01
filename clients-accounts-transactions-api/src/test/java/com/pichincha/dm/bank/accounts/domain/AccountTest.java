package com.pichincha.dm.bank.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTest {

    private UUID accountId;
    private UUID clientId;
    private Long accountNumber;
    private BigDecimal initBalance;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Client mockClient;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        accountNumber = 123456789L;
        initBalance = new BigDecimal("1000.00");
        currentBalance = new BigDecimal("750.50");
        createdAt = LocalDateTime.now().minusDays(1);
        updatedAt = LocalDateTime.now();
        mockClient = Client.builder().id(clientId).username("testuser").state(true).build();
    }

    @Test
    void givenValidDataWhenBuildAccountThenAccountIsCreated() {
        // Arrange & Act
        Account account =
                Account.builder()
                        .id(accountId)
                        .clientId(clientId)
                        .accountNumber(accountNumber)
                        .type(AccountType.AHORROS)
                        .initBalance(initBalance)
                        .currentBalance(currentBalance)
                        .state(true)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .client(mockClient)
                        .build();

        // Assert
        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertEquals(clientId, account.getClientId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(AccountType.AHORROS, account.getType());
        assertEquals(initBalance, account.getInitBalance());
        assertEquals(currentBalance, account.getCurrentBalance());
        assertTrue(account.getState());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
        assertEquals(mockClient, account.getClient());
    }

    @Test
    void givenMinimalDataWhenBuildAccountThenAccountIsCreatedWithNulls() {
        // Arrange & Act
        Account account = Account.builder().id(accountId).type(AccountType.CORRIENTE).build();

        // Assert
        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertEquals(AccountType.CORRIENTE, account.getType());
        assertNull(account.getClientId());
        assertNull(account.getAccountNumber());
        assertNull(account.getInitBalance());
        assertNull(account.getCurrentBalance());
        assertNull(account.getState());
        assertNull(account.getCreatedAt());
        assertNull(account.getUpdatedAt());
        assertNull(account.getClient());
    }

    @Test
    void givenAccountWhenSetIdThenIdIsUpdated() {
        // Arrange
        Account account = Account.builder().build();
        UUID newId = UUID.randomUUID();

        // Act
        account.setId(newId);
        account.setClientId(newId);
        account.setAccountNumber(accountNumber);
        account.setType(AccountType.CORRIENTE);
        account.setInitBalance(initBalance);
        account.setCurrentBalance(currentBalance);
        account.setState(false);
        account.setCreatedAt(createdAt);
        account.setUpdatedAt(updatedAt);
        account.setClient(mockClient);

        // Assert
        assertEquals(newId, account.getId());
        assertEquals(newId, account.getClientId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(AccountType.CORRIENTE, account.getType());
        assertEquals(initBalance, account.getInitBalance());
        assertEquals(currentBalance, account.getCurrentBalance());
        assertEquals(false, account.getState());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
        assertEquals(mockClient, account.getClient());
    }
}
