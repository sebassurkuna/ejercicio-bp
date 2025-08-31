package com.pichincha.dm.bank.accounts.application.port.output;

import com.pichincha.dm.bank.accounts.domain.Client;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientOutputPort {

    Mono<Client> save(Client client);

    Mono<Client> findById(UUID id);

    Flux<Client> findAll();

    Mono<Client> update(Client client);

    Mono<Boolean> existsById(UUID id);

    Mono<Void> deleteById(UUID id);
}
