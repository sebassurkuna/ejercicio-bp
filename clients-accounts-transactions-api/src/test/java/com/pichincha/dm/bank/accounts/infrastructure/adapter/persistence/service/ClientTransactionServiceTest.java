package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.PersonEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.ClientEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.PersonEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.ClientRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.PersonRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ClientTransactionServiceTest {

    @Mock private ClientRepository clientRepository;

    @Mock private PersonRepository personRepository;

    @Mock private ClientEntityMapper clientMapper;

    @Mock private PersonEntityMapper personMapper;

    @InjectMocks private ClientTransactionService clientTransactionService;

    private Client testClient;
    private Person testPerson;
    private ClientEntity testClientEntity;
    private PersonEntity testPersonEntity;
    private ClientEntity savedClientEntity;
    private PersonEntity savedPersonEntity;
    private Client savedClient;

    @BeforeEach
    void setUp() {
        UUID clientId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();

        testPerson =
                Person.builder()
                        .id(personId)
                        .name("Juan")
                        .lastName("Pérez")
                        .gender(Gender.MASCULINO)
                        .birthDate(LocalDate.of(1990, 5, 15))
                        .identification("1234567890")
                        .phone("+593987654321")
                        .address("Av. Amazonas 123, Quito")
                        .state(true)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .updatedAt(LocalDateTime.of(2024, 1, 15, 15, 30))
                        .build();

        testClient =
                Client.builder()
                        .id(clientId)
                        .person(testPerson)
                        .username("juan.perez")
                        .password("securePassword123")
                        .state(true)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .updatedAt(LocalDateTime.of(2024, 1, 15, 15, 30))
                        .build();

        testPersonEntity =
                new PersonEntity(
                        personId,
                        "Juan",
                        "Pérez",
                        "MALE",
                        LocalDate.of(1990, 5, 15),
                        "1234567890",
                        "+593987654321",
                        "Av. Amazonas 123, Quito",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        testClientEntity =
                new ClientEntity(
                        clientId,
                        personId,
                        "juan.perez",
                        "securePassword123",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        savedPersonEntity =
                new PersonEntity(
                        UUID.randomUUID(),
                        "Juan",
                        "Pérez",
                        "MALE",
                        LocalDate.of(1990, 5, 15),
                        "1234567890",
                        "+593987654321",
                        "Av. Amazonas 123, Quito",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        savedClientEntity =
                new ClientEntity(
                        UUID.randomUUID(),
                        savedPersonEntity.getId(),
                        "juan.perez",
                        "securePassword123",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        savedClient =
                Client.builder()
                        .id(savedClientEntity.getId())
                        .person(Person.builder().id(savedPersonEntity.getId()).build())
                        .build();
    }

    @Test
    void givenValidClientWhenSaveClientThenReturnSavedClientMono() {
        // Arrange
        doReturn(testPersonEntity).when(personMapper).toEntity(testPerson);
        doReturn(Mono.just(savedPersonEntity)).when(personRepository).save(testPersonEntity);
        doReturn(testClientEntity)
                .when(clientMapper)
                .toEntity(testClient, savedPersonEntity.getId());
        doReturn(Mono.just(savedClientEntity)).when(clientRepository).save(testClientEntity);
        doReturn(savedClient).when(clientMapper).toDomain(savedClientEntity, savedPersonEntity);

        // Act
        Mono<Client> result = clientTransactionService.saveClient(testClient);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        client -> {
                            assertAll(
                                    () -> assertNotNull(client),
                                    () -> assertEquals(savedClient.getId(), client.getId()),
                                    () ->
                                            assertEquals(
                                                    savedClient.getUsername(),
                                                    client.getUsername()),
                                    () -> assertEquals(savedClient.getState(), client.getState()),
                                    () -> assertNotNull(client.getPerson()),
                                    () ->
                                            assertEquals(
                                                    savedClient.getPerson().getId(),
                                                    client.getPerson().getId()),
                                    () ->
                                            assertEquals(
                                                    savedClient.getPerson().getName(),
                                                    client.getPerson().getName()),
                                    () ->
                                            assertEquals(
                                                    savedClient.getPerson().getLastName(),
                                                    client.getPerson().getLastName()));
                        })
                .verifyComplete();

        verify(personMapper).toEntity(testPerson);
        verify(personRepository).save(testPersonEntity);
        verify(clientMapper).toEntity(testClient, savedPersonEntity.getId());
        verify(clientRepository).save(testClientEntity);
        verify(clientMapper).toDomain(savedClientEntity, savedPersonEntity);
    }

    @Test
    void givenValidClientWhenUpdateClientThenReturnUpdatedClientMono() {
        // Arrange
        Client updatedClient =
                Client.builder()
                        .id(UUID.randomUUID())
                        .state(false)
                        .person(
                                Person.builder()
                                        .name("Juan Carlos")
                                        .phone("+593123456789")
                                        .gender(Gender.MASCULINO)
                                        .birthDate(LocalDate.now())
                                        .identification("1234567890")
                                        .address("Av. Amazonas 123, Quito")
                                        .state(true)
                                        .build())
                        .build();

        PersonEntity existingPersonEntity =
                new PersonEntity(
                        testPerson.getId(),
                        "Juan",
                        "Pérez",
                        "MALE",
                        LocalDate.of(1990, 5, 15),
                        "1234567890",
                        "+593987654321",
                        "Av. Amazonas 123, Quito",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        ClientEntity existingClientEntity =
                new ClientEntity(
                        testClient.getId(),
                        testPerson.getId(),
                        "juan.perez",
                        "securePassword123",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        PersonEntity updatedPersonEntity =
                new PersonEntity(
                        testPerson.getId(),
                        "Juan Carlos",
                        "Pérez",
                        "MALE",
                        LocalDate.of(1990, 5, 15),
                        "1234567890",
                        "+593123456789",
                        "Av. Amazonas 123, Quito",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.now());

        ClientEntity updatedClientEntity =
                new ClientEntity(
                        testClient.getId(),
                        testPerson.getId(),
                        "juan.perez",
                        "securePassword123",
                        false,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.now());

        doReturn(Mono.just(existingClientEntity)).when(clientRepository).findById(any(UUID.class));
        doReturn(Mono.just(existingPersonEntity))
                .when(personRepository)
                .findById(testPerson.getId());
        doReturn(Mono.just(updatedPersonEntity)).when(personRepository).save(existingPersonEntity);
        doReturn(Mono.just(updatedClientEntity)).when(clientRepository).save(existingClientEntity);
        doReturn(updatedClient)
                .when(clientMapper)
                .toDomain(updatedClientEntity, updatedPersonEntity);

        // Act
        Mono<Client> result = clientTransactionService.updateClient(updatedClient);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        client -> {
                            assertAll(
                                    () -> assertNotNull(client),
                                    () -> assertEquals(updatedClient.getId(), client.getId()),
                                    () -> assertEquals(updatedClient.getState(), client.getState()),
                                    () ->
                                            assertEquals(
                                                    updatedClient.getPerson().getName(),
                                                    client.getPerson().getName()),
                                    () ->
                                            assertEquals(
                                                    updatedClient.getPerson().getPhone(),
                                                    client.getPerson().getPhone()));
                        })
                .verifyComplete();
    }

    @Test
    void givenValidClientIdWhenDeleteClientThenReturnMonoVoid() {
        // Arrange
        UUID clientIdToDelete = testClient.getId();
        ClientEntity existingClient =
                new ClientEntity(
                        clientIdToDelete,
                        testPerson.getId(),
                        "juan.perez",
                        "securePassword123",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        doReturn(Mono.just(existingClient)).when(clientRepository).findById(clientIdToDelete);
        doReturn(Mono.empty()).when(clientRepository).deleteById(clientIdToDelete);
        doReturn(Mono.empty()).when(personRepository).deleteById(testPerson.getId());

        // Act
        Mono<Void> result = clientTransactionService.deleteClient(clientIdToDelete);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(clientRepository).findById(clientIdToDelete);
        verify(clientRepository).deleteById(clientIdToDelete);
        verify(personRepository).deleteById(testPerson.getId());
    }
}
