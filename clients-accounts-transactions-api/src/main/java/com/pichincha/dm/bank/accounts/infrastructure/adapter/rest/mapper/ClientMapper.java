package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper;

import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ClienteUpdateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.PersonaCreateDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.PersonaDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.PersonaUpdateDto;
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
public interface ClientMapper {

    @Mapping(
            target = "createdAt",
            source = "createdAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(
            target = "updatedAt",
            source = "updatedAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "estado", source = "state")
    @Mapping(target = "persona", source = "person", qualifiedByName = "toPersonDto")
    ClienteDto toDto(Client client);

    @Named("toPersonDto")
    @Mapping(target = "nombre", source = "name")
    @Mapping(target = "apellido", source = "lastName")
    @Mapping(target = "genero", source = "gender", qualifiedByName = "genderToGeneroEnum")
    @Mapping(target = "fechaNacimiento", source = "birthDate")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "telefono", source = "phone")
    @Mapping(target = "direccion", source = "address")
    @Mapping(target = "estado", source = "state")
    @Mapping(
            target = "createdAt",
            source = "createdAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(
            target = "updatedAt",
            source = "updatedAt",
            qualifiedByName = "localDateTimeToOffsetDateTime")
    PersonaDto toDto(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", source = "estado")
    @Mapping(target = "person", source = "persona")
    Client toDomain(ClienteCreateDto clienteCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "lastName", source = "apellido")
    @Mapping(target = "gender", source = "genero", qualifiedByName = "generoEnumToGender")
    @Mapping(target = "birthDate", source = "fechaNacimiento")
    @Mapping(target = "identification", source = "identificacion")
    @Mapping(target = "phone", source = "telefono")
    @Mapping(target = "address", source = "direccion")
    @Mapping(target = "state", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Person toDomain(PersonaCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personaId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "state", source = "estado")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "person", source = "persona")
    Client toDomain(ClienteUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "lastName", source = "apellido")
    @Mapping(target = "gender", source = "genero", qualifiedByName = "generoUpdateEnumToGender")
    @Mapping(target = "birthDate", source = "fechaNacimiento")
    @Mapping(target = "identification", source = "identificacion")
    @Mapping(target = "phone", source = "telefono")
    @Mapping(target = "address", source = "direccion")
    @Mapping(target = "state", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Person toDomain(PersonaUpdateDto dto);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("generoEnumToGender")
    default Gender generoEnumToGender(PersonaCreateDto.GeneroEnum generoEnum) {
        if (generoEnum == null) {
            return null;
        }
        return switch (generoEnum) {
            case MASCULINO -> Gender.MASCULINO;
            case FEMENINO -> Gender.FEMENINO;
            case OTRO -> Gender.OTRO;
        };
    }

    @Named("genderToGeneroEnum")
    default PersonaDto.GeneroEnum genderToGeneroEnum(Gender gender) {
        if (gender == null) {
            return null;
        }
        return switch (gender) {
            case MASCULINO -> PersonaDto.GeneroEnum.MASCULINO;
            case FEMENINO -> PersonaDto.GeneroEnum.FEMENINO;
            case OTRO -> PersonaDto.GeneroEnum.OTRO;
        };
    }

    @Named("generoUpdateEnumToGender")
    default Gender generoUpdateEnumToGender(PersonaUpdateDto.GeneroEnum generoEnum) {
        if (generoEnum == null) {
            return null;
        }
        return switch (generoEnum) {
            case MASCULINO -> Gender.MASCULINO;
            case FEMENINO -> Gender.FEMENINO;
            case OTRO -> Gender.OTRO;
        };
    }
}
