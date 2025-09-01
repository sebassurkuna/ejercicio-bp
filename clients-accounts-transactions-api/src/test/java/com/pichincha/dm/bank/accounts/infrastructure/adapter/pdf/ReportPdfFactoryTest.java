package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.pichincha.dm.bank.accounts.domain.Report;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportPdfFactoryTest {

    private Report testReport;
    private Report.AccountSummary testAccountSummary;
    private Report.MovementSummary testMovementSummary;

    @BeforeEach
    void setUp() {
        testMovementSummary =
                Report.MovementSummary.builder()
                        .date(LocalDate.of(2024, 1, 15))
                        .type("DEPOSITO")
                        .amount(BigDecimal.valueOf(500.00))
                        .balanceAfterTransaction(BigDecimal.valueOf(1500.00))
                        .build();

        testAccountSummary =
                Report.AccountSummary.builder()
                        .accountNumber(1001L)
                        .accountType("AHORRO")
                        .initialBalance(BigDecimal.valueOf(1000.00))
                        .currentBalance(BigDecimal.valueOf(1500.00))
                        .movements(Arrays.asList(testMovementSummary))
                        .build();

        testReport =
                Report.builder()
                        .clientId(UUID.randomUUID())
                        .clientName("Juan Pérez")
                        .startDate(LocalDate.of(2024, 1, 1))
                        .endDate(LocalDate.of(2024, 1, 31))
                        .accounts(Arrays.asList(testAccountSummary))
                        .totalDebits(BigDecimal.valueOf(200.00))
                        .totalCredits(BigDecimal.valueOf(500.00))
                        .build();
    }

    @Test
    void givenValidReportWhenCreateAccountStatementPdfThenReturnCompleteHtml() {
        try (MockedStatic<PdfDocumentBuilder> builderStatic =
                mockStatic(PdfDocumentBuilder.class)) {
            // Arrange

            builderStatic
                    .when(
                            () ->
                                    PdfDocumentBuilder.formatDateRange(
                                            any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn("01/01/2024 - 31/01/2024");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatMoney(any(BigDecimal.class)))
                    .thenReturn("$1,000.00");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatDate(any(LocalDate.class)))
                    .thenReturn("15/01/2024");

            // Act
            String result = ReportPdfFactory.createAccountStatementPdf(testReport);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.contains("BANCO DEL PICHINCHA")));
        }
    }

    @Test
    void givenReportWithEmptyAccountsWhenCreateAccountStatementPdfThenReturnHtmlWithEmptyMessage() {
        try (MockedStatic<PdfDocumentBuilder> builderStatic =
                mockStatic(PdfDocumentBuilder.class)) {
            // Arrange
            Report emptyAccountsReport = Report.builder().accounts(new ArrayList<>()).build();

            builderStatic
                    .when(
                            () ->
                                    PdfDocumentBuilder.formatDateRange(
                                            any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn("01/01/2024 - 31/01/2024");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatMoney(any(BigDecimal.class)))
                    .thenReturn("$0.00");

            // Act
            String result = ReportPdfFactory.createAccountStatementPdf(emptyAccountsReport);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.contains("No hay cuentas para mostrar")));
        }
    }

    @Test
    void
            givenReportWithAccountsWithoutMovementsWhenCreateAccountStatementPdfThenReturnHtmlWithNoMovementsMessage() {
        try (MockedStatic<PdfDocumentBuilder> builderStatic =
                mockStatic(PdfDocumentBuilder.class)) {
            // Arrange
            Report.AccountSummary accountWithoutMovements =
                    Report.AccountSummary.builder().movements(new ArrayList<>()).build();
            Report reportWithoutMovements =
                    Report.builder().accounts(Arrays.asList(accountWithoutMovements)).build();

            builderStatic
                    .when(
                            () ->
                                    PdfDocumentBuilder.formatDateRange(
                                            any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn("01/01/2024 - 31/01/2024");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatMoney(any(BigDecimal.class)))
                    .thenReturn("$1,000.00");

            // Act
            String result = ReportPdfFactory.createAccountStatementPdf(reportWithoutMovements);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () ->
                            assertTrue(
                                    result.contains(
                                            "No hay movimientos para mostrar en el período"
                                                    + " seleccionado")));
        }
    }

    @Test
    void givenValidReportWhenCreateAccountSummaryPdfThenReturnSummaryHtml() {
        try (MockedStatic<PdfDocumentBuilder> builderStatic =
                mockStatic(PdfDocumentBuilder.class)) {
            // Arrange

            builderStatic
                    .when(
                            () ->
                                    PdfDocumentBuilder.formatDateRange(
                                            any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn("01/01/2024 - 31/01/2024");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatMoney(any(BigDecimal.class)))
                    .thenReturn("$1,000.00");

            // Act
            String result = ReportPdfFactory.createAccountSummaryPdf(testReport);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.contains("Resumen de Cuentas")));
        }
    }

    @Test
    void
            givenReportWithEmptyAccountsWhenCreateAccountSummaryPdfThenReturnSummaryWithEmptyMessage() {
        try (MockedStatic<PdfDocumentBuilder> builderStatic =
                mockStatic(PdfDocumentBuilder.class)) {
            // Arrange
            Report emptyAccountsReport = Report.builder().accounts(Collections.emptyList()).build();

            builderStatic
                    .when(
                            () ->
                                    PdfDocumentBuilder.formatDateRange(
                                            any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn("01/01/2024 - 31/01/2024");
            builderStatic
                    .when(() -> PdfDocumentBuilder.formatMoney(any(BigDecimal.class)))
                    .thenReturn("$0.00");

            // Act
            String result = ReportPdfFactory.createAccountSummaryPdf(emptyAccountsReport);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.contains("No hay cuentas para mostrar")));
        }
    }
}
