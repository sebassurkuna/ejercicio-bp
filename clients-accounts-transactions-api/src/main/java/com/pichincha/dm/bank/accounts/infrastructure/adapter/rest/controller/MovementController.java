package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichincha.dm.bank.accounts.application.port.input.MovementInputPort;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.api.MovementsApi;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoUpdateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper.MovementMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MovementController implements MovementsApi {

    private final MovementInputPort inputPort;
    private final MovementMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ResponseEntity<MovimientoDto>> createMovement(
            Mono<MovimientoCreateDto> movimientoCreateDto, ServerWebExchange exchange) {
        return movimientoCreateDto
                .map(mapper::toDomain)
                .flatMap(inputPort::createMovement)
                .map(mapper::toDto)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(
            UUID movimientoId, ServerWebExchange exchange) {
        return inputPort
                .deleteMovement(movimientoId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<MovimientoDto>> getMovementById(
            UUID movimientoId, ServerWebExchange exchange) {
        return inputPort.getMovementById(movimientoId).map(mapper::toDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<MovimientoDto>>> listMovements(
            UUID clienteId,
            String numeroCuenta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer page,
            Integer size,
            ServerWebExchange exchange) {

        return Mono.just(
                ResponseEntity.ok(
                        inputPort
                                .listMovements(
                                        clienteId,
                                        Long.valueOf(numeroCuenta),
                                        fechaDesde,
                                        fechaHasta,
                                        page,
                                        size)
                                .map(mapper::toDto)));
    }

    @Override
    public Mono<ResponseEntity<MovimientoDto>> updateMovement(
            UUID movimientoId,
            Mono<MovimientoUpdateDto> movimientoUpdateDto,
            ServerWebExchange exchange) {
        return inputPort
                .getMovementById(movimientoId)
                .zipWith(movimientoUpdateDto)
                .map(
                        tuple -> {
                            Movement existing = tuple.getT1();
                            MovimientoUpdateDto updateDto = tuple.getT2();
                            return getMovementToUpdate(existing, updateDto);
                        })
                .flatMap(inputPort::updateMovement)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    private static Movement getMovementToUpdate(Movement existing, MovimientoUpdateDto updateDto) {
        return Movement.builder()
                .id(existing.getId())
                .accountId(existing.getAccountId())
                .date(existing.getDate())
                .type(getMovementTypeToUpdate(updateDto.getValor()))
                .value(updateDto.getValor() != null ? updateDto.getValor() : existing.getValue())
                .postMovementBalance(existing.getPostMovementBalance())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    private static MovementType getMovementTypeToUpdate(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0 ? MovementType.CREDITO : MovementType.DEBITO;
    }
}
