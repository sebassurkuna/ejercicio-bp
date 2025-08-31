package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends R2dbcRepository<ClientEntity, UUID> {}
