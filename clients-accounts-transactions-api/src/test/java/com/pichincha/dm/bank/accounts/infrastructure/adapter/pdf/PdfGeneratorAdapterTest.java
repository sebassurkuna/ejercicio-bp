package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.pichincha.dm.bank.accounts.domain.Report;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PdfGeneratorAdapterTest {

    @InjectMocks private PdfGeneratorAdapter pdfGeneratorAdapter;

    private Report testReport;

    @BeforeEach
    void setUp() {
        testReport = createTestReport();
    }

    @Test
    void givenValidReportWhenGeneratePdfFromReportThenReturnBase64String() {
        // Arrange
        String expectedHtml = "<html><body>Test HTML Content</body></html>";
        String expectedBase64 = Base64.getEncoder().encodeToString("test pdf content".getBytes());

        try (MockedStatic<ReportPdfFactory> factoryMock = mockStatic(ReportPdfFactory.class)) {
            factoryMock
                    .when(() -> ReportPdfFactory.createAccountStatementPdf(any(Report.class)))
                    .thenReturn(expectedHtml);

            // Act
            Mono<String> result = pdfGeneratorAdapter.generatePdfFromReport(testReport);

            // Assert
            StepVerifier.create(result)
                    .assertNext(
                            base64String -> {
                                assertNotNull(base64String);
                                assertFalse(base64String.isEmpty());
                                try {
                                    Base64.getDecoder().decode(base64String);
                                } catch (IllegalArgumentException e) {
                                    throw new AssertionError("String is not valid Base64", e);
                                }
                            })
                    .verifyComplete();

            factoryMock.verify(
                    () -> ReportPdfFactory.createAccountStatementPdf(testReport), times(1));
        }
    }

    @Test
    void givenReportPdfFactoryThrowsExceptionWhenGeneratePdfFromReportThenReturnError() {
        // Arrange
        RuntimeException factoryException = new RuntimeException("Factory error occurred");

        try (MockedStatic<ReportPdfFactory> factoryMock = mockStatic(ReportPdfFactory.class)) {
            factoryMock
                    .when(() -> ReportPdfFactory.createAccountStatementPdf(any(Report.class)))
                    .thenThrow(factoryException);

            // Act
            Mono<String> result = pdfGeneratorAdapter.generatePdfFromReport(testReport);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(
                            throwable ->
                                    throwable instanceof RuntimeException
                                            && throwable
                                                    .getMessage()
                                                    .contains("Error generating PDF")
                                            && throwable
                                                    .getMessage()
                                                    .contains("Factory error occurred")
                                            && throwable.getCause() == factoryException)
                    .verify();
        }
    }

    private Report createTestReport() {
        return Report.builder()
                .clientId(UUID.randomUUID())
                .clientName("Juan Pérez")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .accounts(new ArrayList<>())
                .totalDebits(new BigDecimal("500.00"))
                .totalCredits(new BigDecimal("1000.00"))
                .build();
    }
}
