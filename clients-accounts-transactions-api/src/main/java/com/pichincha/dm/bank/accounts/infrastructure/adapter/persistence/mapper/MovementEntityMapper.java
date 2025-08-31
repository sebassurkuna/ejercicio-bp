package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper;

import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MovementEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "postMovementBalance", source = "postMovementBalance")
    @Mapping(target = "createdAt", source = "createdAt")
    Movement toDomain(MovementEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "postMovementBalance", source = "postMovementBalance")
    @Mapping(target = "createdAt", source = "createdAt")
    MovementEntity toEntity(Movement domain);
}
