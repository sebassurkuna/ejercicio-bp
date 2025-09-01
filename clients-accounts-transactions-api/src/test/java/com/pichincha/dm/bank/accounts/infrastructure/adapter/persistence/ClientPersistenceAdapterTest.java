package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.PersonEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.ClientEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.ClientRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.PersonRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.ClientTransactionService;
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
class ClientPersistenceAdapterTest {

    @Mock private ClientRepository clientRepository;

    @Mock private PersonRepository personRepository;

    @Mock private ClientEntityMapper clientMapper;

    @Mock private ClientTransactionService transactionService;

    @InjectMocks private ClientPersistenceAdapter clientPersistenceAdapter;

    private Client testClient;
    private Person testPerson;
    private ClientEntity testClientEntity;
    private PersonEntity testPersonEntity;
    private UUID testClientId;
    private UUID testPersonId;

    @BeforeEach
    void setUp() {
        testClientId = UUID.randomUUID();
        testPersonId = UUID.randomUUID();

        testPerson =
                Person.builder()
                        .id(testPersonId)
                        .name("Juan Carlos")
                        .gender(Gender.MASCULINO)
                        .birthDate(LocalDate.now())
                        .identification("1234567890")
                        .address("Av. Principal 123")
                        .phone("0987654321")
                        .build();

        testClient =
                Client.builder()
                        .id(testClientId)
                        .personaId(testPersonId)
                        .person(testPerson)
                        .password("encryptedPassword123")
                        .state(true)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .updatedAt(LocalDateTime.of(2024, 1, 15, 15, 30))
                        .build();

        testPersonEntity =
                new PersonEntity(
                        testPersonId,
                        "Juan",
                        "Carlos",
                        Gender.MASCULINO.name(),
                        LocalDate.now(),
                        "1234567890",
                        "Av. Principal 123",
                        "0987654321",
                        false,
                        LocalDateTime.of(2024, 1, 1, 9, 0),
                        LocalDateTime.of(2024, 1, 10, 14, 30));

        testClientEntity =
                new ClientEntity(
                        testClientId,
                        testPersonId,
                        "CLI001",
                        "encryptedPassword123",
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));
    }

    @Test
    void givenValidClientWhenSaveThenReturnSavedClientMono() {
        // Arrange
        Client savedClient = Client.builder().id(UUID.randomUUID()).build();

        doReturn(Mono.just(savedClient)).when(transactionService).saveClient(testClient);
        doReturn(Mono.just(testClientEntity)).when(clientRepository).findById(savedClient.getId());
        doReturn(Mono.just(testPersonEntity)).when(personRepository).findById(testPersonId);
        doReturn(savedClient).when(clientMapper).toDomain(testClientEntity, testPersonEntity);

        // Act
        Mono<Client> result = clientPersistenceAdapter.save(testClient);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        client -> {
                            assertAll(
                                    () -> assertNotNull(client),
                                    () -> assertEquals(savedClient.getId(), client.getId()),
                                    () ->
                                            assertEquals(
                                                    savedClient.getPersonaId(),
                                                    client.getPersonaId()),
                                    () -> assertEquals(savedClient.getId(), client.getId()),
                                    () -> assertEquals(savedClient.getState(), client.getState()));
                        })
                .verifyComplete();

        verify(transactionService).saveClient(testClient);
        verify(clientRepository).findById(savedClient.getId());
        verify(personRepository).findById(testPersonId);
        verify(clientMapper).toDomain(testClientEntity, testPersonEntity);
    }

    @Test
    void givenExistingClientsWhenFindAllThenReturnClientsFlux() {
        // Arrange
        UUID secondClientId = UUID.randomUUID();
        UUID secondPersonId = UUID.randomUUID();

        Person secondPerson =
                Person.builder()
                        .id(secondPersonId)
                        .name("Maria Lopez")
                        .gender(Gender.FEMENINO)
                        .identification("0987654321")
                        .build();

        Client secondClient =
                Client.builder()
                        .id(secondClientId)
                        .personaId(secondPersonId)
                        .person(secondPerson)
                        .build();

        PersonEntity secondPersonEntity =
                new PersonEntity(
                        secondPersonId,
                        "Maria",
                        "Lopez",
                        Gender.FEMENINO.name(),
                        LocalDate.now(),
                        "0987654321",
                        "Calle Secundaria 456",
                        "0123456789",
                        false,
                        LocalDateTime.of(2024, 1, 2, 9, 0),
                        LocalDateTime.of(2024, 1, 12, 14, 30));

        ClientEntity secondClientEntity =
                new ClientEntity(
                        secondClientId,
                        secondPersonId,
                        "CLI002",
                        "encryptedPassword456",
                        true,
                        LocalDateTime.of(2024, 1, 2, 11, 0),
                        LocalDateTime.of(2024, 1, 16, 16, 30));

        Flux<ClientEntity> clientEntities = Flux.just(testClientEntity, secondClientEntity);

        doReturn(clientEntities).when(clientRepository).findAll();
        doReturn(Mono.just(testPersonEntity)).when(personRepository).findById(testPersonId);
        doReturn(Mono.just(secondPersonEntity)).when(personRepository).findById(secondPersonId);
        doReturn(testClient).when(clientMapper).toDomain(testClientEntity, testPersonEntity);
        doReturn(secondClient).when(clientMapper).toDomain(secondClientEntity, secondPersonEntity);

        // Act
        Flux<Client> result = clientPersistenceAdapter.findAll();

        // Assert
        StepVerifier.create(result)
                .assertNext(client -> assertEquals(testClient.getId(), client.getId()))
                .assertNext(client -> assertEquals(secondClient.getId(), client.getId()))
                .verifyComplete();

        verify(clientRepository).findAll();
        verify(personRepository).findById(testPersonId);
        verify(personRepository).findById(secondPersonId);
        verify(clientMapper).toDomain(testClientEntity, testPersonEntity);
        verify(clientMapper).toDomain(secondClientEntity, secondPersonEntity);
    }

    @Test
    void givenValidClientWhenUpdateThenReturnUpdatedClientMono() {
        // Arrange
        Client clientToUpdate = Client.builder().password("newEncryptedPassword").build();

        Client updatedClient = Client.builder().updatedAt(LocalDateTime.now()).build();

        doReturn(Mono.just(updatedClient)).when(transactionService).updateClient(clientToUpdate);
        doReturn(Mono.just(testClientEntity))
                .when(clientRepository)
                .findById(updatedClient.getId());
        doReturn(Mono.just(testPersonEntity)).when(personRepository).findById(testPersonId);
        doReturn(updatedClient).when(clientMapper).toDomain(testClientEntity, testPersonEntity);

        // Act
        Mono<Client> result = clientPersistenceAdapter.update(clientToUpdate);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        client -> {
                            assertAll(
                                    () -> assertNotNull(client),
                                    () ->
                                            assertEquals(
                                                    updatedClient.getPassword(),
                                                    client.getPassword()),
                                    () -> assertNotNull(client.getUpdatedAt()));
                        })
                .verifyComplete();

        verify(transactionService).updateClient(clientToUpdate);
        verify(clientRepository).findById(updatedClient.getId());
        verify(personRepository).findById(testPersonId);
        verify(clientMapper).toDomain(testClientEntity, testPersonEntity);
    }

    @Test
    void givenValidIdWhenExistsByIdThenReturnTrueMono() {
        // Arrange
        doReturn(Mono.just(true)).when(clientRepository).existsById(testClientId);

        // Act
        Mono<Boolean> result = clientPersistenceAdapter.existsById(testClientId);

        // Assert
        StepVerifier.create(result).assertNext(exists -> assertTrue(exists)).verifyComplete();

        verify(clientRepository).existsById(testClientId);
    }

    @Test
    void givenValidIdWhenDeleteByIdThenReturnMonoVoid() {
        // Arrange
        doReturn(Mono.empty()).when(transactionService).deleteClient(testClientId);

        // Act
        Mono<Void> result = clientPersistenceAdapter.deleteById(testClientId);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(transactionService).deleteClient(testClientId);
    }
}
