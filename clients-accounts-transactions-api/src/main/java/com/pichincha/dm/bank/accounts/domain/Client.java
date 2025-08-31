package com.pichincha.dm.bank.accounts.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
public class Client {
    private UUID id;
    private UUID personaId;
    private String username;
    private String password;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Person person;
}
