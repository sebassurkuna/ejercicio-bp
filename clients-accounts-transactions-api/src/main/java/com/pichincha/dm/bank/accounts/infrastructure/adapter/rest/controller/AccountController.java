package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.controller;

import com.pichincha.dm.bank.accounts.application.port.input.AccountInputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.api.AccountsApi;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaUpdateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper.AccountMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountInputPort inputPort;
    private final AccountMapper mapper;

    @Override
    public Mono<ResponseEntity<CuentaDto>> createAccount(
            Mono<CuentaCreateDto> cuentaCreateDto, ServerWebExchange exchange) {
        return cuentaCreateDto
                .map(mapper::toDomain)
                .flatMap(inputPort::createAccount)
                .map(mapper::toDto)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            String numeroCuenta, ServerWebExchange exchange) {
        return inputPort
                .deleteAccount(numeroCuenta)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<CuentaDto>> getAccountByNumber(
            String numeroCuenta, ServerWebExchange exchange) {
        return inputPort
                .getAccountByNumber(numeroCuenta)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<CuentaDto>>> listAccounts(
            UUID clienteId, Integer page, Integer size, ServerWebExchange exchange) {

        return Mono.just(
                ResponseEntity.ok(
                        inputPort.listAccounts(clienteId, page, size).map(mapper::toDto)));
    }

    @Override
    public Mono<ResponseEntity<CuentaDto>> updateAccount(
            String numeroCuenta,
            Mono<CuentaUpdateDto> cuentaUpdateDto,
            ServerWebExchange exchange) {
        return inputPort
                .getAccountByNumber(numeroCuenta)
                .zipWith(cuentaUpdateDto)
                .map(
                        tuple -> {
                            Account existing = tuple.getT1();
                            CuentaUpdateDto updateDto = tuple.getT2();
                            return getAccountToUpdate(existing, updateDto);
                        })
                .flatMap(inputPort::updateAccount)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    private Account getAccountToUpdate(Account existing, CuentaUpdateDto updateDto) {
        return Account.builder()
                .id(existing.getId())
                .clientId(existing.getClientId())
                .accountNumber(existing.getAccountNumber())
                .type(
                        updateDto.getTipo() != null
                                ? mapper.tipoEnumToAccountType(updateDto.getTipo())
                                : existing.getType())
                .initBalance(existing.getInitBalance())
                .currentBalance(existing.getCurrentBalance())
                .state(updateDto.getEstado() != null ? updateDto.getEstado() : existing.getState())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();
    }
}
