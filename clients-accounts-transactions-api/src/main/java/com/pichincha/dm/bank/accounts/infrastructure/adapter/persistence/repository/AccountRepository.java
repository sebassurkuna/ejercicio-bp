package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<AccountEntity, UUID> {

    Mono<AccountEntity> findByAccountNumber(Long accountNumber);

    @Query(
            "SELECT * FROM bank.cuenta "
                    + "WHERE (:clientId IS NULL OR cliente_id = :clientId) "
                    + "ORDER BY created_at DESC "
                    + "LIMIT :size OFFSET :offset")
    Flux<AccountEntity> findWithFilters(UUID clientId, int size, int offset);

    Mono<Void> deleteByAccountNumber(Long accountNumber);
}
