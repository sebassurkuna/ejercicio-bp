package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity;

import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
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

@Table(name = "movimiento", schema = "bank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovementEntity {

    @Id private UUID id;

    @Column("cuenta_id")
    private UUID accountId;

    @Column("fecha")
    private LocalDateTime date;

    @Column("tipo")
    private MovementType type;

    @Column("valor")
    private BigDecimal value;

    @Column("saldo_post_movimiento")
    private BigDecimal postMovementBalance;

    @Column("created_at")
    private LocalDateTime createdAt;
}
