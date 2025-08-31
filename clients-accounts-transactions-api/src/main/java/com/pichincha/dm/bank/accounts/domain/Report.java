package com.pichincha.dm.bank.accounts.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Report {

    private UUID clientId;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountSummary> accounts;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;

    @Data
    @Builder
    public static class AccountSummary {
        private Long accountNumber;
        private String accountType;
        private BigDecimal initialBalance;
        private BigDecimal currentBalance;
        private BigDecimal totalDebits;
        private BigDecimal totalCredits;
        private List<MovementSummary> movements;
    }

    @Data
    @Builder
    public static class MovementSummary {
        private UUID id;
        private LocalDate date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balanceAfterTransaction;
    }
}
