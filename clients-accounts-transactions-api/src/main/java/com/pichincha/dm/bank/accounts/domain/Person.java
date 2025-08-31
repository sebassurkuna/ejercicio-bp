package com.pichincha.dm.bank.accounts.domain;

import com.pichincha.dm.bank.accounts.domain.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Person {
    private UUID id;
    private String name;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;
    private String identification;
    private String phone;
    private String address;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
