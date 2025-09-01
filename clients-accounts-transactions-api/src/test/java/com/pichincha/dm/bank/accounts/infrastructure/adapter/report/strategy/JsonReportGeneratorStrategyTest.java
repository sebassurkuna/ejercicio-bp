package com.pichincha.dm.bank.accounts.infrastructure.adapter.report.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pichincha.dm.bank.accounts.domain.Report;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class JsonReportGeneratorStrategyTest {

    private JsonReportGeneratorStrategy jsonReportGeneratorStrategy;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jsonReportGeneratorStrategy = new JsonReportGeneratorStrategy();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnJsonFormatType() {
        // Act
        String result = jsonReportGeneratorStrategy.getFormatType();

        // Assert
        assertEquals("json", result);
    }

    @Test
    void shouldGenerateJsonReportWhenValidReportProvided() throws JsonProcessingException {
        // Arrange
        Report report = createValidReport();

        // Act
        Mono<String> result = jsonReportGeneratorStrategy.generateReport(report);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        jsonString -> {
                            assertNotNull(jsonString);
                            assertTrue(jsonString.length() > 0);
                        })
                .verifyComplete();
    }

    private Report createValidReport() {
        UUID movementId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Report.MovementSummary movementSummary =
                Report.MovementSummary.builder()
                        .id(movementId)
                        .date(LocalDate.of(2024, 1, 15))
                        .type("CREDITO")
                        .amount(BigDecimal.valueOf(500))
                        .balanceAfterTransaction(BigDecimal.valueOf(1500))
                        .build();

        Report.AccountSummary accountSummary =
                Report.AccountSummary.builder()
                        .accountNumber(123456L)
                        .accountType("AHORROS")
                        .initialBalance(BigDecimal.valueOf(1000))
                        .currentBalance(BigDecimal.valueOf(1500))
                        .totalDebits(BigDecimal.ZERO)
                        .totalCredits(BigDecimal.valueOf(500))
                        .movements(List.of(movementSummary))
                        .build();

        return Report.builder()
                .clientId(clientId)
                .clientName("Juan Pérez")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .accounts(List.of(accountSummary))
                .totalDebits(BigDecimal.ZERO)
                .totalCredits(BigDecimal.valueOf(500))
                .build();
    }
}
