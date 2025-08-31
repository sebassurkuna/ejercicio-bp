package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.MovementEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementTransactionService {

    private final MovementRepository movementRepository;
    private final MovementEntityMapper movementMapper;

    @Transactional
    public Mono<Movement> saveMovement(Movement movement) {
        MovementEntity entity = movementMapper.toEntity(movement);
        return movementRepository.save(entity).map(movementMapper::toDomain);
    }
}
