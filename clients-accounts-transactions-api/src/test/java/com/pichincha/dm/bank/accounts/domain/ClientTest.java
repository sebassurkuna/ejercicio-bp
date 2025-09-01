package com.pichincha.dm.bank.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientTest {

    private UUID clientId;
    private UUID personaId;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Person mockPerson;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        personaId = UUID.randomUUID();
        username = "testclient";
        password = "securepassword123";
        createdAt = LocalDateTime.now().minusDays(2);
        updatedAt = LocalDateTime.now();
        mockPerson =
                Person.builder()
                        .id(personaId)
                        .name("John")
                        .lastName("Doe")
                        .identification("1234567890")
                        .build();
    }

    @Test
    void givenValidDataWhenBuildClientThenClientIsCreated() {
        // Arrange & Act
        Client client =
                Client.builder()
                        .id(clientId)
                        .personaId(personaId)
                        .username(username)
                        .password(password)
                        .state(true)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .person(mockPerson)
                        .build();

        // Assert
        assertNotNull(client);
        assertEquals(clientId, client.getId());
        assertEquals(personaId, client.getPersonaId());
        assertEquals(username, client.getUsername());
        assertEquals(password, client.getPassword());
        assertTrue(client.getState());
        assertEquals(createdAt, client.getCreatedAt());
        assertEquals(updatedAt, client.getUpdatedAt());
        assertEquals(mockPerson, client.getPerson());
    }

    @Test
    void givenClientWhenSetIdThenIdIsUpdated() {
        // Arrange
        Client client = Client.builder().build();
        UUID newId = UUID.randomUUID();

        // Act
        client.setId(newId);
        client.setPersonaId(newId);
        client.setUsername(username);
        client.setPassword(password);
        client.setState(false);
        client.setCreatedAt(createdAt);
        client.setUpdatedAt(updatedAt);
        client.setPerson(mockPerson);
        // Assert
        assertEquals(newId, client.getId());
        assertEquals(newId, client.getPersonaId());
        assertEquals(username, client.getUsername());
        assertEquals(password, client.getPassword());
        assertEquals(false, client.getState());
        assertEquals(createdAt, client.getCreatedAt());
        assertEquals(updatedAt, client.getUpdatedAt());
        assertEquals(mockPerson, client.getPerson());
    }
}
