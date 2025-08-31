package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import com.pichincha.dm.bank.accounts.application.port.output.ClientOutputPort;
import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.ClientEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.ClientRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.PersonRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.ClientTransactionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ClientPersistenceAdapter implements ClientOutputPort {

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final ClientEntityMapper clientMapper;
    private final ClientTransactionService transactionService;

    @Override
    public Mono<Client> save(Client client) {
        return transactionService
                .saveClient(client)
                .flatMap(clientSaved -> findById(clientSaved.getId()));
    }

    @Override
    public Mono<Client> findById(UUID id) {
        return clientRepository
                .findById(id)
                .flatMap(
                        clientEntity ->
                                personRepository
                                        .findById(clientEntity.getPersonId())
                                        .map(
                                                personEntity ->
                                                        clientMapper.toDomain(
                                                                clientEntity, personEntity)));
    }

    @Override
    public Flux<Client> findAll() {
        return clientRepository
                .findAll()
                .flatMap(
                        clientEntity ->
                                personRepository
                                        .findById(clientEntity.getPersonId())
                                        .map(
                                                personEntity ->
                                                        clientMapper.toDomain(
                                                                clientEntity, personEntity)));
    }

    @Override
    public Mono<Client> update(Client client) {
        return transactionService
                .updateClient(client)
                .flatMap(clientUpdated -> findById(clientUpdated.getId()));
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return clientRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return transactionService.deleteClient(id);
    }
}
