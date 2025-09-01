package com.pichincha.dm.bank.accounts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import com.pichincha.dm.bank.accounts.application.port.input.AccountInputPort;
import com.pichincha.dm.bank.accounts.application.port.input.ClientInputPort;
import com.pichincha.dm.bank.accounts.application.port.input.MovementInputPort;
import com.pichincha.dm.bank.accounts.application.strategy.ReportGeneratorStrategyFactory;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Client;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.Report;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ReportApplicationServiceTest {

    @Mock private ClientInputPort clientInputPort;

    @Mock private AccountInputPort accountInputPort;

    @Mock private MovementInputPort movementInputPort;

    @Mock private ReportGeneratorStrategyFactory strategyFactory;

    @InjectMocks private ReportApplicationService reportApplicationService;

    private UUID clientId;
    private UUID accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Client mockClient;
    private Account mockAccount;
    private Movement mockMovement;
    private Person mockPerson;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);

        mockPerson = Person.builder().id(UUID.randomUUID()).name("Juan").lastName("Perez").build();

        mockClient = Client.builder().id(clientId).person(mockPerson).build();

        mockAccount =
                Account.builder()
                        .id(accountId)
                        .clientId(clientId)
                        .accountNumber(123456L)
                        .type(AccountType.AHORROS)
                        .initBalance(new BigDecimal("1000.00"))
                        .currentBalance(new BigDecimal("1200.00"))
                        .build();

        mockMovement =
                Movement.builder()
                        .id(UUID.randomUUID())
                        .accountId(accountId)
                        .date(LocalDateTime.of(2024, 1, 15, 10, 0))
                        .type(MovementType.CREDITO)
                        .value(new BigDecimal("200.00"))
                        .postMovementBalance(new BigDecimal("1200.00"))
                        .build();
    }

    @Test
    void givenValidParametersWhenGenerateReportThenReturnReport() {
        // Arrange
        doReturn(Mono.just(mockClient)).when(clientInputPort).getClientById(clientId);
        doReturn(Flux.just(mockAccount)).when(accountInputPort).listAccounts(clientId, null, null);
        doReturn(Flux.just(mockMovement))
                .when(movementInputPort)
                .listMovements(any(), eq(123456L), eq(startDate), eq(endDate), any(), any());

        // Act & Assert
        StepVerifier.create(reportApplicationService.generateReport(clientId, startDate, endDate))
                .expectNextMatches(
                        report -> {
                            assertEquals(clientId, report.getClientId());
                            assertEquals("Juan Perez", report.getClientName());
                            assertEquals(startDate, report.getStartDate());
                            assertEquals(endDate, report.getEndDate());
                            assertEquals(1, report.getAccounts().size());
                            assertNotNull(report.getTotalDebits());
                            assertNotNull(report.getTotalCredits());
                            return true;
                        })
                .verifyComplete();
    }

    @Test
    void givenInvalidDateRangeWhenGenerateReportThenThrowException() {
        // Arrange
        LocalDate invalidStartDate = LocalDate.of(2024, 2, 1);
        LocalDate invalidEndDate = LocalDate.of(2024, 1, 31);
        doReturn(Mono.just(mockClient)).when(clientInputPort).getClientById(clientId);
        // Act & Assert
        StepVerifier.create(
                        reportApplicationService.generateReport(
                                clientId, invalidStartDate, invalidEndDate))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void givenValidParametersWhenGenerateFormattedReportThenReturnFormattedReport() {
        // Arrange
        String format = "PDF";
        String expectedFormattedReport = "formatted-report-content";

        doReturn(Mono.just(mockClient)).when(clientInputPort).getClientById(clientId);
        doReturn(Flux.just(mockAccount)).when(accountInputPort).listAccounts(clientId, null, null);
        doReturn(Flux.just(mockMovement))
                .when(movementInputPort)
                .listMovements(any(), eq(123456L), eq(startDate), eq(endDate), any(), any());
        doReturn(Mono.just(expectedFormattedReport))
                .when(strategyFactory)
                .generateReport(any(Report.class), eq(format));

        // Act & Assert
        StepVerifier.create(
                        reportApplicationService.generateFormattedReport(
                                clientId, startDate, endDate, format))
                .expectNext(expectedFormattedReport)
                .verifyComplete();
    }
}
