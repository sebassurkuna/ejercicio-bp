package com.pichincha.dm.bank.accounts.infrastructure.adapter.report.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.pichincha.dm.bank.accounts.application.port.output.ReportOutputPort;
import com.pichincha.dm.bank.accounts.domain.Report;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PdfReportGeneratorStrategyTest {

    @Mock private ReportOutputPort reportOutputPort;

    @InjectMocks private PdfReportGeneratorStrategy pdfReportGeneratorStrategy;

    private Report testReport;
    private UUID testClientId;

    @BeforeEach
    void setUp() {
        testClientId = UUID.randomUUID();

        Report.MovementSummary movementSummary =
                Report.MovementSummary.builder()
                        .id(UUID.randomUUID())
                        .date(LocalDate.of(2024, 1, 15))
                        .type("CREDITO")
                        .amount(BigDecimal.valueOf(500.00))
                        .balanceAfterTransaction(BigDecimal.valueOf(1500.00))
                        .build();

        Report.AccountSummary accountSummary =
                Report.AccountSummary.builder()
                        .accountNumber(1001L)
                        .accountType("AHORROS")
                        .initialBalance(BigDecimal.valueOf(1000.00))
                        .currentBalance(BigDecimal.valueOf(1500.00))
                        .totalDebits(BigDecimal.ZERO)
                        .totalCredits(BigDecimal.valueOf(500.00))
                        .movements(Arrays.asList(movementSummary))
                        .build();

        testReport =
                Report.builder()
                        .clientId(testClientId)
                        .clientName("Ana María González")
                        .startDate(LocalDate.of(2024, 1, 1))
                        .endDate(LocalDate.of(2024, 1, 31))
                        .accounts(Arrays.asList(accountSummary))
                        .totalDebits(BigDecimal.ZERO)
                        .totalCredits(BigDecimal.valueOf(500.00))
                        .build();
    }

    @Test
    void givenValidReportWhenGenerateReportThenReturnPdfStringMono() {
        // Arrange
        String expectedPdfPath = "/reports/client_" + testClientId + "_2024-01-01_2024-01-31.pdf";

        doReturn(Mono.just(expectedPdfPath))
                .when(reportOutputPort)
                .generatePdfFromReport(testReport);

        // Act
        Mono<String> result = pdfReportGeneratorStrategy.generateReport(testReport);

        // Assert
        StepVerifier.create(result).expectNext(expectedPdfPath).verifyComplete();

        verify(reportOutputPort).generatePdfFromReport(testReport);
        verifyNoMoreInteractions(reportOutputPort);
    }

    @Test
    void whenGetFormatTypeThenReturnPdfString() {
        // Act
        String formatType = pdfReportGeneratorStrategy.getFormatType();

        // Assert
        assertEquals("pdf", formatType);
    }
}
