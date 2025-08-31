package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cliente", schema = "bank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {

    @Id private UUID id;

    @Column("persona_id")
    private UUID personId;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("estado")
    private Boolean state;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
