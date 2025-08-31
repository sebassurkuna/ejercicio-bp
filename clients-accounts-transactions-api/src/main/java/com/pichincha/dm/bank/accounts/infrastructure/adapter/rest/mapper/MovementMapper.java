package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper;

import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoUpdateDto;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovementMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cuentaId", source = "accountId")
    @Mapping(target = "fecha", source = "date", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "tipo", source = "type", qualifiedByName = "movementTypeToTipoEnum")
    @Mapping(target = "valor", source = "value")
    @Mapping(target = "saldoPostMovimiento", source = "postMovementBalance")
    @Mapping(
            target = "createdAt",
            source = "createdAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    MovimientoDto toDto(Movement movement);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", source = "cuentaId")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", source = "tipo", qualifiedByName = "tipoEnumToMovementType")
    @Mapping(target = "value", source = "valor")
    @Mapping(target = "postMovementBalance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "account", ignore = true)
    Movement toDomain(MovimientoCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "value", source = "valor")
    @Mapping(target = "postMovementBalance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "account", ignore = true)
    Movement toDomain(MovimientoUpdateDto updateDto);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.of("-05:00")) : null;
    }

    @Named("movementTypeToTipoEnum")
    default MovimientoDto.TipoEnum movementTypeToTipoEnum(MovementType movementType) {
        if (movementType == null) {
            return null;
        }
        return switch (movementType) {
            case DEBITO -> MovimientoDto.TipoEnum.DEBITO;
            case CREDITO -> MovimientoDto.TipoEnum.CREDITO;
        };
    }

    @Named("tipoEnumToMovementType")
    default MovementType tipoEnumToMovementType(MovimientoCreateDto.TipoEnum tipoEnum) {
        if (tipoEnum == null) {
            return null;
        }
        return switch (tipoEnum) {
            case DEBITO -> MovementType.DEBITO;
            case CREDITO -> MovementType.CREDITO;
        };
    }
}
