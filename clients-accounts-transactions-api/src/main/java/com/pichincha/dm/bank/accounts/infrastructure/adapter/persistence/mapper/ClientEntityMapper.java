package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper;

import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.PersonEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ClientEntityMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "personaId", source = "entity.personId")
    @Mapping(target = "username", source = "entity.username")
    @Mapping(target = "password", source = "entity.password")
    @Mapping(target = "state", source = "entity.state")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "person", source = "personEntity", qualifiedByName = "personEntityToPerson")
    Client toDomain(ClientEntity entity, PersonEntity personEntity);

    @Mapping(target = "personId", source = "personId")
    @Mapping(target = "id", source = "client.id")
    @Mapping(target = "username", source = "client.username")
    @Mapping(target = "password", source = "client.password")
    @Mapping(target = "state", source = "client.state")
    @Mapping(target = "createdAt", source = "client.createdAt")
    @Mapping(target = "updatedAt", source = "client.updatedAt")
    ClientEntity toEntity(Client client, UUID personId);

    @Named("personEntityToPerson")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "stringToGender")
    Person personEntityToPerson(PersonEntity personEntity);

    @Named("stringToGender")
    default Gender stringToGender(String gender) {
        if (gender == null) {
            return null;
        }
        return switch (gender) {
            case "MASCULINO" -> Gender.MASCULINO;
            case "FEMENINO" -> Gender.FEMENINO;
            case "OTRO" -> Gender.OTRO;
            default -> throw new IllegalArgumentException("Unexpected gender value: " + gender);
        };
    }
}
