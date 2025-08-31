package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AccountEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "initBalance", source = "initBalance")
    @Mapping(target = "currentBalance", source = "currentBalance")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Account toDomain(AccountEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "initBalance", source = "initBalance")
    @Mapping(target = "currentBalance", source = "currentBalance")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    AccountEntity toEntity(Account domain);
}
