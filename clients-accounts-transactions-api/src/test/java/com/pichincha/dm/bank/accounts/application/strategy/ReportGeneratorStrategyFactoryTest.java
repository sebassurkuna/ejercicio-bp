package com.pichincha.dm.bank.accounts.application.strategy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.pichincha.dm.bank.accounts.domain.Report;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
class ReportGeneratorStrategyFactoryTest {

    @Mock private ReportGeneratorStrategy jsonStrategy;

    @Mock private ReportGeneratorStrategy pdfStrategy;

    @InjectMocks private ReportGeneratorStrategyFactory reportGeneratorStrategyFactory;

    private Report mockReport;
    private List<ReportGeneratorStrategy> strategies;

    @BeforeEach
    void setUp() {
        mockReport =
                Report.builder()
                        .clientId(UUID.randomUUID())
                        .clientName("Juan Perez")
                        .startDate(LocalDate.of(2024, 1, 1))
                        .endDate(LocalDate.of(2024, 1, 31))
                        .accounts(Collections.emptyList())
                        .totalDebits(BigDecimal.ZERO)
                        .totalCredits(BigDecimal.ZERO)
                        .build();

        strategies = Arrays.asList(jsonStrategy, pdfStrategy);

        doReturn("json").when(jsonStrategy).getFormatType();
        doReturn("pdf").when(pdfStrategy).getFormatType();

        try {
            var strategiesField =
                    ReportGeneratorStrategyFactory.class.getDeclaredField("strategies");
            strategiesField.setAccessible(true);
            strategiesField.set(reportGeneratorStrategyFactory, strategies);
        } catch (Exception e) {
            throw new RuntimeException("Error configurando estrategias para test", e);
        }
    }

    @Test
    void givenValidJsonFormatWhenGenerateReportThenReturnJsonReport() {
        // Arrange
        String expectedJsonReport = "{\"clientId\":\"123\",\"clientName\":\"Juan Perez\"}";
        doReturn(Mono.just(expectedJsonReport))
                .when(jsonStrategy)
                .generateReport(any(Report.class));

        // Act & Assert
        StepVerifier.create(reportGeneratorStrategyFactory.generateReport(mockReport, "json"))
                .expectNext(expectedJsonReport)
                .verifyComplete();
    }

    @Test
    void givenUnsupportedFormatWhenGenerateReportThenThrowException() {
        // Arrange
        String unsupportedFormat = "xml";

        // Act & Assert
        StepVerifier.create(
                        reportGeneratorStrategyFactory.generateReport(
                                mockReport, unsupportedFormat))
                .expectErrorMatches(
                        throwable -> {
                            boolean isIllegalArgumentException =
                                    throwable instanceof IllegalArgumentException;
                            boolean hasExpectedMessage =
                                    throwable.getMessage().contains("Formato no soportado: xml");
                            boolean containsAvailableFormats =
                                    throwable.getMessage().contains("Formatos disponibles:");
                            return isIllegalArgumentException
                                    && hasExpectedMessage
                                    && containsAvailableFormats;
                        })
                .verify();
    }

    @Test
    void givenDuplicateFormatStrategiesWhenGenerateReportThenUseFirst() {
        // Arrange
        ReportGeneratorStrategy duplicateJsonStrategy =
                org.mockito.Mockito.mock(ReportGeneratorStrategy.class);
        doReturn("json").when(duplicateJsonStrategy).getFormatType();

        List<ReportGeneratorStrategy> strategiesWithDuplicate =
                Arrays.asList(jsonStrategy, duplicateJsonStrategy, pdfStrategy);

        try {
            var strategiesField =
                    ReportGeneratorStrategyFactory.class.getDeclaredField("strategies");
            strategiesField.setAccessible(true);
            strategiesField.set(reportGeneratorStrategyFactory, strategiesWithDuplicate);
        } catch (Exception e) {
            throw new RuntimeException("Error configurando estrategias duplicadas para test", e);
        }

        String expectedJsonReport = "original-json-strategy-result";
        doReturn(Mono.just(expectedJsonReport))
                .when(jsonStrategy)
                .generateReport(any(Report.class));

        // Act & Assert
        StepVerifier.create(reportGeneratorStrategyFactory.generateReport(mockReport, "json"))
                .expectNext(expectedJsonReport)
                .verifyComplete();
    }
}
