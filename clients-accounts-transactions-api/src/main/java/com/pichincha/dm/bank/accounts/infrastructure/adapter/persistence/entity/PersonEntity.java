package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "persona", schema = "bank")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonEntity {

    @Id private UUID id;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String lastName;

    @Column("genero")
    private String gender;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("identificacion")
    private String identification;

    @Column("telefono")
    private String phone;

    @Column("direccion")
    private String address;

    @Column("estado")
    private Boolean state;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
