package com.pichincha.dm.bank.accounts.application.port.input;

import com.pichincha.dm.bank.accounts.domain.Movement;
import java.time.LocalDate;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementInputPort {

    Mono<Movement> createMovement(Movement movement);

    Mono<Movement> getMovementById(UUID id);

    Flux<Movement> listMovements(
            UUID clientId,
            Long accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size);

    Mono<Movement> updateMovement(Movement movement);

    Mono<Void> deleteMovement(UUID id);
}
