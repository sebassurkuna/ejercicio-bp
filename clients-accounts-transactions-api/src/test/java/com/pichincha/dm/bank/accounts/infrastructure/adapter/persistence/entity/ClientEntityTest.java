package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientEntityTest {

    private ClientEntity clientEntity;
    private UUID testId;
    private UUID testPersonId;
    private String testUsername;
    private String testPassword;
    private Boolean testState;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testPersonId = UUID.randomUUID();
        testUsername = "john.doe";
        testPassword = "securePassword123";
        testState = true;
        testCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        testUpdatedAt = LocalDateTime.of(2024, 1, 15, 15, 30);

        clientEntity = new ClientEntity();
    }

    @Test
    void givenAllArgsConstructorWhenCreateClientEntityThenAllFieldsAreSet() {
        // Arrange & Act
        ClientEntity entity =
                new ClientEntity(
                        testId,
                        testPersonId,
                        testUsername,
                        testPassword,
                        testState,
                        testCreatedAt,
                        testUpdatedAt);

        // Assert
        assertAll(
                () -> assertEquals(testId, entity.getId()),
                () -> assertEquals(testPersonId, entity.getPersonId()),
                () -> assertEquals(testUsername, entity.getUsername()),
                () -> assertEquals(testPassword, entity.getPassword()),
                () -> assertEquals(testState, entity.getState()),
                () -> assertEquals(testCreatedAt, entity.getCreatedAt()),
                () -> assertEquals(testUpdatedAt, entity.getUpdatedAt()));
    }

    @Test
    void givenClientEntityWhenSetIdThenIdIsUpdated() {
        // Arrange
        UUID newId = UUID.randomUUID();

        // Act
        clientEntity.setId(newId);
        clientEntity.setPersonId(testPersonId);
        clientEntity.setUsername(testUsername);
        clientEntity.setPassword(testPassword);
        clientEntity.setState(testState);
        clientEntity.setCreatedAt(testCreatedAt);
        clientEntity.setUpdatedAt(testUpdatedAt);

        // Assert
        assertEquals(newId, clientEntity.getId());
        assertEquals(testPersonId, clientEntity.getPersonId());
        assertEquals(testUsername, clientEntity.getUsername());
        assertEquals(testPassword, clientEntity.getPassword());
        assertEquals(testState, clientEntity.getState());
        assertEquals(testCreatedAt, clientEntity.getCreatedAt());
        assertEquals(testUpdatedAt, clientEntity.getUpdatedAt());
    }
}
