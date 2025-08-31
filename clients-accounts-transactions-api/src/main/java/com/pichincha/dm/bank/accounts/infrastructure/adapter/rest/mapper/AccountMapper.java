package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.CuentaUpdateDto;
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
public interface AccountMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clienteId", source = "clientId")
    @Mapping(target = "numeroCuenta", source = "accountNumber")
    @Mapping(target = "tipo", source = "type", qualifiedByName = "accountTypeToTipoEnum")
    @Mapping(target = "saldoInicial", source = "initBalance")
    @Mapping(target = "saldoActual", source = "currentBalance")
    @Mapping(target = "estado", source = "state")
    @Mapping(
            target = "createdAt",
            source = "createdAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(
            target = "updatedAt",
            source = "updatedAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    CuentaDto toDto(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", source = "clienteId")
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "type", source = "tipo", qualifiedByName = "tipoEnumToAccountType")
    @Mapping(target = "initBalance", source = "saldoInicial")
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    Account toDomain(CuentaCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "type", source = "tipo", qualifiedByName = "tipoEnumToAccountType")
    @Mapping(target = "initBalance", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "state", source = "estado")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    Account toDomain(CuentaUpdateDto updateDto);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.of("-05:00")) : null;
    }

    @Named("accountTypeToTipoEnum")
    default CuentaDto.TipoEnum accountTypeToTipoEnum(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return switch (accountType) {
            case AHORROS -> CuentaDto.TipoEnum.AHORROS;
            case CORRIENTE -> CuentaDto.TipoEnum.CORRIENTE;
        };
    }

    @Named("tipoEnumToAccountType")
    default AccountType tipoEnumToAccountType(CuentaCreateDto.TipoEnum tipoEnum) {
        if (tipoEnum == null) {
            return null;
        }
        return switch (tipoEnum) {
            case AHORROS -> AccountType.AHORROS;
            case CORRIENTE -> AccountType.CORRIENTE;
        };
    }

    @Named("tipoEnumToAccountType")
    default AccountType tipoEnumToAccountType(CuentaUpdateDto.TipoEnum tipoEnum) {
        if (tipoEnum == null) {
            return null;
        }
        return switch (tipoEnum) {
            case AHORROS -> AccountType.AHORROS;
            case CORRIENTE -> AccountType.CORRIENTE;
        };
    }
}
