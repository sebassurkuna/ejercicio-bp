package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper;

import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PersonEntityMapper {
    @Mapping(target = "id", ignore = true)
    PersonEntity toEntity(Person person);
}
