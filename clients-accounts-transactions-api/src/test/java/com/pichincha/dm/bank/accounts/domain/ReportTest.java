package com.pichincha.dm.bank.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportTest {

    private UUID clientId;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private List<Report.AccountSummary> accounts;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        clientName = "John Doe";
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);
        totalDebits = new BigDecimal("1500.00");
        totalCredits = new BigDecimal("2000.00");
        accounts = new ArrayList<>();
    }

    @Test
    void givenValidDataWhenBuildReportThenReportIsCreated() {
        // Arrange & Act
        Report report =
                Report.builder()
                        .clientId(clientId)
                        .clientName(clientName)
                        .startDate(startDate)
                        .endDate(endDate)
                        .accounts(accounts)
                        .totalDebits(totalDebits)
                        .totalCredits(totalCredits)
                        .build();

        // Assert
        assertNotNull(report);
        assertEquals(clientId, report.getClientId());
        assertEquals(clientName, report.getClientName());
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(accounts, report.getAccounts());
        assertEquals(totalDebits, report.getTotalDebits());
        assertEquals(totalCredits, report.getTotalCredits());
    }

    @Test
    void givenReportWhenSetClientIdThenClientIdIsUpdated() {
        // Arrange
        Report report = Report.builder().build();
        UUID newClientId = UUID.randomUUID();

        // Act
        report.setClientId(newClientId);
        report.setClientName(clientName);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setAccounts(accounts);
        report.setTotalDebits(totalDebits);
        report.setTotalCredits(totalCredits);

        // Assert
        assertEquals(newClientId, report.getClientId());
    }
}

// Separate test class for AccountSummary
@ExtendWith(MockitoExtension.class)
class AccountSummaryTest {

    private Long accountNumber;
    private String accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private List<Report.MovementSummary> movements;

    @BeforeEach
    void setUp() {
        accountNumber = 123456789L;
        accountType = "AHORROS";
        initialBalance = new BigDecimal("1000.00");
        currentBalance = new BigDecimal("1500.00");
        totalDebits = new BigDecimal("500.00");
        totalCredits = new BigDecimal("1000.00");
        movements = new ArrayList<>();
    }

    @Test
    void givenValidDataWhenBuildAccountSummaryThenAccountSummaryIsCreated() {
        // Arrange & Act
        Report.AccountSummary accountSummary =
                Report.AccountSummary.builder()
                        .accountNumber(accountNumber)
                        .accountType(accountType)
                        .initialBalance(initialBalance)
                        .currentBalance(currentBalance)
                        .totalDebits(totalDebits)
                        .totalCredits(totalCredits)
                        .movements(movements)
                        .build();

        // Assert
        assertNotNull(accountSummary);
        assertEquals(accountNumber, accountSummary.getAccountNumber());
        assertEquals(accountType, accountSummary.getAccountType());
        assertEquals(initialBalance, accountSummary.getInitialBalance());
        assertEquals(currentBalance, accountSummary.getCurrentBalance());
        assertEquals(totalDebits, accountSummary.getTotalDebits());
        assertEquals(totalCredits, accountSummary.getTotalCredits());
        assertEquals(movements, accountSummary.getMovements());
    }

    @Test
    void givenAccountSummaryWhenSetAccountNumberThenAccountNumberIsUpdated() {
        // Arrange
        Report.AccountSummary accountSummary = Report.AccountSummary.builder().build();
        Long newAccountNumber = 987654321L;

        // Act
        accountSummary.setAccountNumber(newAccountNumber);
        accountSummary.setAccountType(accountType);

        // Assert
        assertEquals(newAccountNumber, accountSummary.getAccountNumber());
    }
}

// Separate test class for MovementSummary
@ExtendWith(MockitoExtension.class)
class MovementSummaryTest {

    private UUID movementId;
    private LocalDate date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;

    @BeforeEach
    void setUp() {
        movementId = UUID.randomUUID();
        date = LocalDate.of(2024, 1, 15);
        type = "CREDITO";
        amount = new BigDecimal("500.00");
        balanceAfterTransaction = new BigDecimal("1500.00");
    }

    @Test
    void givenValidDataWhenBuildMovementSummaryThenMovementSummaryIsCreated() {
        // Arrange & Act
        Report.MovementSummary movementSummary =
                Report.MovementSummary.builder()
                        .id(movementId)
                        .date(date)
                        .type(type)
                        .amount(amount)
                        .balanceAfterTransaction(balanceAfterTransaction)
                        .build();

        // Assert
        assertNotNull(movementSummary);
        assertEquals(movementId, movementSummary.getId());
        assertEquals(date, movementSummary.getDate());
        assertEquals(type, movementSummary.getType());
        assertEquals(amount, movementSummary.getAmount());
        assertEquals(balanceAfterTransaction, movementSummary.getBalanceAfterTransaction());
    }

    @Test
    void givenMovementSummaryWhenSetIdThenIdIsUpdated() {
        // Arrange
        Report.MovementSummary movementSummary = Report.MovementSummary.builder().build();
        UUID newId = UUID.randomUUID();

        // Act
        movementSummary.setId(newId);
        movementSummary.setDate(date);
        movementSummary.setType(type);
        movementSummary.setAmount(amount);
        movementSummary.setBalanceAfterTransaction(balanceAfterTransaction);

        // Assert
        assertEquals(newId, movementSummary.getId());
    }
}
