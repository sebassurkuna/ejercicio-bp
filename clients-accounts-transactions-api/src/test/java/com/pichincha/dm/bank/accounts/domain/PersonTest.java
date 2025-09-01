package com.pichincha.dm.bank.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonTest {

    private UUID personId;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String identification;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        name = "John";
        lastName = "Doe";
        birthDate = LocalDate.of(1990, 5, 15);
        identification = "1234567890";
        phone = "+1-555-123-4567";
        address = "123 Main St, City, State";
        createdAt = LocalDateTime.now().minusDays(7);
        updatedAt = LocalDateTime.now();
    }

    @Test
    void givenValidDataWhenBuildPersonThenPersonIsCreated() {
        // Arrange & Act
        Person person =
                Person.builder()
                        .id(personId)
                        .name(name)
                        .lastName(lastName)
                        .gender(Gender.MASCULINO)
                        .birthDate(birthDate)
                        .identification(identification)
                        .phone(phone)
                        .address(address)
                        .state(true)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .build();

        // Assert
        assertNotNull(person);
        assertEquals(personId, person.getId());
        assertEquals(name, person.getName());
        assertEquals(lastName, person.getLastName());
        assertEquals(Gender.MASCULINO, person.getGender());
        assertEquals(birthDate, person.getBirthDate());
        assertEquals(identification, person.getIdentification());
        assertEquals(phone, person.getPhone());
        assertEquals(address, person.getAddress());
        assertTrue(person.getState());
        assertEquals(createdAt, person.getCreatedAt());
        assertEquals(updatedAt, person.getUpdatedAt());
    }
}
