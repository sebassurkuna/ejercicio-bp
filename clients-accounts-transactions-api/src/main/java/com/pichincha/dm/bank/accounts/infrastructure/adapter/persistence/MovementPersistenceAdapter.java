package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.MovementEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.MovementRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.MovementTransactionService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MovementPersistenceAdapter implements MovementOutputPort {

    private final MovementRepository movementRepository;
    private final MovementEntityMapper movementMapper;
    private final MovementTransactionService transactionService;

    @Override
    public Mono<Movement> save(Movement movement) {
        return transactionService
                .saveMovement(movement)
                .flatMap(movementSaved -> findById(movementSaved.getId()));
    }

    @Override
    public Mono<Movement> findById(UUID id) {
        return movementRepository.findById(id).map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> findAll(
            UUID clientId,
            Long accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59, 999999999) : null;

        int pageSize = size != null ? size : 50;
        int offset = page != null ? page * pageSize : 0;

        return movementRepository
                .findWithFilters(
                        clientId, accountNumber, startDateTime, endDateTime, pageSize, offset)
                .map(movementMapper::toDomain);
    }

    @Override
    public Mono<Movement> update(Movement movement) {
        return movementRepository
                .save(movementMapper.toEntity(movement))
                .map(movementMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return movementRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return movementRepository.deleteById(id);
    }

    @Override
    public Flux<Movement> findMovementsAfterDate(UUID accountId, LocalDateTime fromDateTime) {
        return movementRepository
                .findByAccountIdAndDateAfter(accountId, fromDateTime)
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> updateBatch(List<Movement> movements) {
        return Flux.fromIterable(movements)
                .map(movementMapper::toEntity)
                .flatMap(movementRepository::save)
                .map(movementMapper::toDomain);
    }
}
