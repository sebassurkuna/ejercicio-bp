package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.controller;

import com.pichincha.dm.bank.accounts.application.port.input.ClientInputPort;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.api.ClientsApi;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteUpdateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper.ClientMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController implements ClientsApi {

    private final ClientInputPort inputPort;
    private final ClientMapper mapper;

    @Override
    public Mono<ResponseEntity<ClienteDto>> createClient(
            Mono<ClienteCreateDto> clientCreateDto, ServerWebExchange exchange) {
        return clientCreateDto
                .map(mapper::toDomain)
                .flatMap(inputPort::createClient)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteClient(UUID clientId, ServerWebExchange exchange) {
        return inputPort
                .deleteClient(clientId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<ClienteDto>> getClientById(
            UUID clientId, ServerWebExchange exchange) {
        return inputPort.getClientById(clientId).map(mapper::toDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<ClienteDto>>> listClients(
            Integer page, Integer size, ServerWebExchange exchange) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        long skip = (long) pageNumber * pageSize;

        return Mono.just(
                ResponseEntity.ok(
                        inputPort
                                .getAllClients()
                                .sort(
                                        (client1, client2) ->
                                                client2.getCreatedAt()
                                                        .compareTo(client1.getCreatedAt()))
                                .skip(skip)
                                .take(pageSize)
                                .map(mapper::toDto)));
    }

    @Override
    public Mono<ResponseEntity<ClienteDto>> updateClient(
            UUID clientId, Mono<ClienteUpdateDto> clientUpdateDto, ServerWebExchange exchange) {
        return clientUpdateDto
                .map(mapper::toDomain)
                .doOnNext(client -> client.setId(clientId))
                .flatMap(inputPort::updateClient)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }
}
