package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cuenta", schema = "bank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

    @Id private UUID id;

    @Column("cliente_id")
    private UUID clientId;

    @Column("numero_cuenta")
    private Long accountNumber;

    @Column("tipo")
    private AccountType type;

    @Column("saldo_inicial")
    private BigDecimal initBalance;

    @Column("saldo_actual")
    private BigDecimal currentBalance;

    @Column("estado")
    private Boolean state;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
