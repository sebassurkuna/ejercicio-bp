package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonEntityTest {

    private PersonEntity personEntity;
    private UUID testId;
    private String testName;
    private String testLastName;
    private String testGender;
    private LocalDate testBirthDate;
    private String testIdentification;
    private String testPhone;
    private String testAddress;
    private Boolean testState;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testName = "Juan";
        testLastName = "Pérez";
        testGender = "M";
        testBirthDate = LocalDate.of(1990, 5, 15);
        testIdentification = "1234567890";
        testPhone = "+593987654321";
        testAddress = "Av. Amazonas 123, Quito";
        testState = true;
        testCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        testUpdatedAt = LocalDateTime.of(2024, 1, 15, 15, 30);

        personEntity = new PersonEntity();
    }

    @Test
    void givenAllArgsConstructorWhenCreatePersonEntityThenAllFieldsAreSet() {
        // Arrange & Act
        PersonEntity entity =
                new PersonEntity(
                        testId,
                        testName,
                        testLastName,
                        testGender,
                        testBirthDate,
                        testIdentification,
                        testPhone,
                        testAddress,
                        testState,
                        testCreatedAt,
                        testUpdatedAt);

        // Assert
        assertAll(
                () -> assertEquals(testId, entity.getId()),
                () -> assertEquals(testName, entity.getName()),
                () -> assertEquals(testLastName, entity.getLastName()),
                () -> assertEquals(testGender, entity.getGender()),
                () -> assertEquals(testBirthDate, entity.getBirthDate()),
                () -> assertEquals(testIdentification, entity.getIdentification()),
                () -> assertEquals(testPhone, entity.getPhone()),
                () -> assertEquals(testAddress, entity.getAddress()),
                () -> assertEquals(testState, entity.getState()),
                () -> assertEquals(testCreatedAt, entity.getCreatedAt()),
                () -> assertEquals(testUpdatedAt, entity.getUpdatedAt()));
    }

    @Test
    void givenPersonEntityWhenSetIdThenIdIsUpdated() {
        // Arrange
        UUID newId = UUID.randomUUID();

        // Act
        personEntity.setId(newId);
        personEntity.setName(testName);
        personEntity.setLastName(testLastName);
        personEntity.setGender(testGender);
        personEntity.setBirthDate(testBirthDate);
        personEntity.setIdentification(testIdentification);
        personEntity.setPhone(testPhone);
        personEntity.setAddress(testAddress);
        personEntity.setState(testState);
        personEntity.setCreatedAt(testCreatedAt);
        personEntity.setUpdatedAt(testUpdatedAt);

        // Assert
        assertEquals(newId, personEntity.getId());
        assertEquals(testName, personEntity.getName());
        assertEquals(testLastName, personEntity.getLastName());
        assertEquals(testGender, personEntity.getGender());
        assertEquals(testBirthDate, personEntity.getBirthDate());
        assertEquals(testIdentification, personEntity.getIdentification());
        assertEquals(testPhone, personEntity.getPhone());
        assertEquals(testAddress, personEntity.getAddress());
        assertEquals(testState, personEntity.getState());
        assertEquals(testCreatedAt, personEntity.getCreatedAt());
        assertEquals(testUpdatedAt, personEntity.getUpdatedAt());
    }
}
