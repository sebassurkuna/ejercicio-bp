package com.pichincha.dm.bank.accounts.application.port.output;

import com.pichincha.dm.bank.accounts.domain.Movement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementOutputPort {

    Mono<Movement> save(Movement movement);

    Mono<Movement> findById(UUID id);

    Flux<Movement> findAll(
            UUID clientId,
            Long accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size);

    Mono<Movement> update(Movement movement);

    Mono<Boolean> existsById(UUID id);

    Mono<Void> deleteById(UUID id);

    /**
     * Encuentra todos los movimientos posteriores a una fecha específica para una cuenta dada,
     * ordenados por fecha.
     *
     * @param accountId ID de la cuenta
     * @param fromDateTime Fecha y hora desde la cual buscar (exclusivo)
     * @return Flux de movimientos posteriores
     */
    Flux<Movement> findMovementsAfterDate(UUID accountId, LocalDateTime fromDateTime);

    /**
     * Actualiza múltiples movimientos en una operación batch. Útil para optimizar actualizaciones
     * masivas.
     *
     * @param movements Lista de movimientos a actualizar
     * @return Flux de movimientos actualizados
     */
    Flux<Movement> updateBatch(List<Movement> movements);
}
