package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.PersonEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.ClientEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.PersonEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.ClientRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.PersonRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientTransactionService {

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final ClientEntityMapper clientMapper;
    private final PersonEntityMapper personMapper;

    @Transactional
    public Mono<Client> saveClient(Client client) {
        return personRepository
                .save(personMapper.toEntity(client.getPerson()))
                .flatMap(
                        savedPerson ->
                                clientRepository
                                        .save(clientMapper.toEntity(client, savedPerson.getId()))
                                        .map(
                                                clientEntity ->
                                                        clientMapper.toDomain(
                                                                clientEntity, savedPerson)));
    }

    @Transactional
    public Mono<Client> updateClient(Client client) {
        return findExistingClient(client.getId())
                .flatMap(existingClient -> updateClientAndPerson(existingClient, client));
    }

    private Mono<ClientEntity> findExistingClient(UUID clientId) {
        return clientRepository.findById(clientId);
    }

    private Mono<Client> updateClientAndPerson(ClientEntity existingClient, Client client) {
        return findAndUpdatePerson(existingClient.getPersonId(), client)
                .flatMap(savedPerson -> updateAndSaveClient(existingClient, client, savedPerson));
    }

    private Mono<PersonEntity> findAndUpdatePerson(UUID personId, Client client) {
        return personRepository
                .findById(personId)
                .map(
                        existingPerson -> {
                            updatePersonEntity(existingPerson, client);
                            return existingPerson;
                        })
                .flatMap(personRepository::save);
    }

    private Mono<Client> updateAndSaveClient(
            ClientEntity existingClient, Client client, PersonEntity savedPerson) {
        updateClientEntity(existingClient, client);
        return clientRepository
                .save(existingClient)
                .map(savedClient -> clientMapper.toDomain(savedClient, savedPerson));
    }

    @Transactional
    public Mono<Void> deleteClient(UUID id) {
        return clientRepository
                .findById(id)
                .flatMap(
                        existingClient ->
                                clientRepository
                                        .deleteById(id)
                                        .then(
                                                personRepository.deleteById(
                                                        existingClient.getPersonId())));
    }

    private void updatePersonEntity(PersonEntity existingPerson, Client client) {
        existingPerson.setName(client.getPerson().getName());
        existingPerson.setLastName(client.getPerson().getLastName());
        existingPerson.setGender(client.getPerson().getGender().name());
        existingPerson.setBirthDate(client.getPerson().getBirthDate());
        existingPerson.setIdentification(client.getPerson().getIdentification());
        existingPerson.setPhone(client.getPerson().getPhone());
        existingPerson.setAddress(client.getPerson().getAddress());
        existingPerson.setState(client.getPerson().getState());
        existingPerson.setUpdatedAt(LocalDateTime.now());
    }

    private void updateClientEntity(ClientEntity existingClient, Client client) {
        existingClient.setState(client.getState());
        existingClient.setUpdatedAt(LocalDateTime.now());
    }
}
