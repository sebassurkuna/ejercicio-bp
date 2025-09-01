package com.pichincha.dm.bank.accounts.infrastructure.adapter.report.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pichincha.dm.bank.accounts.application.strategy.ReportGeneratorStrategy;
import com.pichincha.dm.bank.accounts.domain.Report;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JsonReportGeneratorStrategy implements ReportGeneratorStrategy {

    private final ObjectMapper objectMapper;

    public JsonReportGeneratorStrategy() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<String> generateReport(Report report) {
        return Mono.fromCallable(() -> convertToJson(report))
                .onErrorMap(
                        ex ->
                                new RuntimeException(
                                        "Error generating JSON report: " + ex.getMessage(), ex));
    }

    @Override
    public String getFormatType() {
        return "json";
    }

    private String convertToJson(Report report) throws JsonProcessingException {
        return objectMapper.writeValueAsString(report);
    }
}
