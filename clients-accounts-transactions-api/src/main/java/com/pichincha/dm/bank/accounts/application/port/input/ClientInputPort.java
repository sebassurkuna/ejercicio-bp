package com.pichincha.dm.bank.accounts.application.port.input;

import com.pichincha.dm.bank.accounts.domain.Client;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientInputPort {

    Flux<Client> getAllClients();

    Mono<Client> getClientById(UUID id);

    Mono<Client> createClient(Client client);

    Mono<Client> updateClient(Client client);

    Mono<Void> deleteClient(UUID id);
}
