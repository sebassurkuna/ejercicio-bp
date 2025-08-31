package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovementRepository extends R2dbcRepository<MovementEntity, UUID> {

    @Query(
            "SELECT m.* FROM bank.movimiento m LEFT JOIN bank.cuenta c ON m.cuenta_id = c.id WHERE"
                + " (:clientId::uuid IS NULL OR c.cliente_id = :clientId::uuid) AND"
                + " (:accountNumber::bigint IS NULL OR c.numero_cuenta = :accountNumber::bigint)"
                + " AND (:startDate::timestamp IS NULL OR m.fecha >= :startDate::timestamp) AND"
                + " (:endDate::timestamp IS NULL OR m.fecha <= :endDate::timestamp) ORDER BY"
                + " m.fecha DESC LIMIT :size OFFSET :offset")
    Flux<MovementEntity> findWithFilters(
            UUID clientId,
            Long accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int size,
            int offset);

    /**
     * Encuentra todos los movimientos posteriores a una fecha específica para una cuenta dada,
     * ordenados por fecha ascendente.
     */
    @Query(
            "SELECT * FROM bank.movimiento WHERE cuenta_id = :accountId::uuid "
                    + "AND fecha > :fromDateTime::timestamp ORDER BY fecha ASC")
    Flux<MovementEntity> findByAccountIdAndDateAfter(UUID accountId, LocalDateTime fromDateTime);
}
