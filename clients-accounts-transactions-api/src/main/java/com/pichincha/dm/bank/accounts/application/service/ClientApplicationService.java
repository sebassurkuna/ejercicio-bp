package com.pichincha.dm.bank.accounts.application.service;

import com.pichincha.dm.bank.accounts.application.port.input.ClientInputPort;
import com.pichincha.dm.bank.accounts.application.port.output.ClientOutputPort;
import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.exception.DataAccessException;
import com.pichincha.dm.bank.accounts.domain.exception.DataModifyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientApplicationService implements ClientInputPort {

    private final ClientOutputPort clientOutputPort;

    @Override
    public Flux<Client> getAllClients() {
        return clientOutputPort.findAll().onErrorMap(DataAccessException::new);
    }

    @Override
    public Mono<Client> getClientById(UUID id) {
        return clientOutputPort.findById(id).onErrorMap(DataAccessException::new);
    }

    @Override
    public Mono<Client> createClient(Client client) {
        client.setState(true);
        return clientOutputPort.save(client).onErrorMap(DataModifyException::new);
    }

    @Override
    public Mono<Client> updateClient(Client client) {
        return clientOutputPort.update(client).onErrorMap(DataModifyException::new);
    }

    @Override
    public Mono<Void> deleteClient(UUID id) {
        return clientOutputPort.deleteById(id).onErrorMap(DataModifyException::new);
    }
}
