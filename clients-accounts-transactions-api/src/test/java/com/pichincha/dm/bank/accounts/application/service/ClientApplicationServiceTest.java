package com.pichincha.dm.bank.accounts.application.service;

import static org.mockito.Mockito.doReturn;

import com.pichincha.dm.bank.accounts.application.port.output.ClientOutputPort;
import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ClientApplicationServiceTest {

    @Mock private ClientOutputPort clientOutputPort;

    @InjectMocks private ClientApplicationService clientApplicationService;

    private Client testClient;
    private UUID testClientId;

    @BeforeEach
    void setUp() {
        testClientId = UUID.randomUUID();
        testClient = createTestClient(testClientId);
    }

    @Test
    void givenValidClientDataWhenGetAllClientsThenReturnAllClients() {
        // Arrange
        Client client1 = createTestClient(UUID.randomUUID());
        Client client2 = createTestClient(UUID.randomUUID());
        Flux<Client> expectedClients = Flux.just(client1, client2);

        doReturn(expectedClients).when(clientOutputPort).findAll();

        // Act
        Flux<Client> result = clientApplicationService.getAllClients();

        // Assert
        StepVerifier.create(result).expectNext(client1).expectNext(client2).verifyComplete();
    }

    @Test
    void givenValidClientIdWhenGetClientByIdThenReturnClient() {
        // Arrange
        doReturn(Mono.just(testClient)).when(clientOutputPort).findById(testClientId);

        // Act
        Mono<Client> result = clientApplicationService.getClientById(testClientId);

        // Assert
        StepVerifier.create(result).expectNext(testClient).verifyComplete();
    }

    @Test
    void givenValidClientWhenCreateClientThenReturnClientWithStateTrue() {
        // Arrange
        Client clientToCreate =
                Client.builder()
                        .username("testuser")
                        .password("password123")
                        .person(createTestPerson())
                        .build();

        Client expectedClient =
                Client.builder()
                        .id(UUID.randomUUID())
                        .username("testuser")
                        .password("password123")
                        .state(true)
                        .person(createTestPerson())
                        .createdAt(LocalDateTime.now())
                        .build();

        doReturn(Mono.just(expectedClient)).when(clientOutputPort).save(clientToCreate);

        // Act
        Mono<Client> result = clientApplicationService.createClient(clientToCreate);

        // Assert
        StepVerifier.create(result).expectNext(expectedClient).verifyComplete();
    }

    @Test
    void givenValidClientWhenUpdateClientThenReturnUpdatedClient() {
        // Arrange
        Client updatedClient =
                Client.builder()
                        .id(testClientId)
                        .username("updateduser")
                        .password("newpassword")
                        .state(true)
                        .person(createTestPerson())
                        .updatedAt(LocalDateTime.now())
                        .build();

        doReturn(Mono.just(updatedClient)).when(clientOutputPort).update(updatedClient);

        // Act
        Mono<Client> result = clientApplicationService.updateClient(updatedClient);

        // Assert
        StepVerifier.create(result).expectNext(updatedClient).verifyComplete();
    }

    @Test
    void givenValidClientIdWhenDeleteClientThenCompleteSuccessfully() {
        // Arrange
        doReturn(Mono.empty()).when(clientOutputPort).deleteById(testClientId);

        // Act
        Mono<Void> result = clientApplicationService.deleteClient(testClientId);

        // Assert
        StepVerifier.create(result).verifyComplete();
    }

    private Client createTestClient(UUID id) {
        return Client.builder()
                .id(id)
                .personaId(UUID.randomUUID())
                .username("testuser")
                .password("password123")
                .state(true)
                .person(createTestPerson())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Person createTestPerson() {
        return Person.builder()
                .id(UUID.randomUUID())
                .name("Juan")
                .lastName("Pérez")
                .gender(Gender.MASCULINO)
                .birthDate(LocalDate.of(1990, 5, 15))
                .identification("1234567890")
                .phone("0987654321")
                .address("Av. Principal 123")
                .state(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
