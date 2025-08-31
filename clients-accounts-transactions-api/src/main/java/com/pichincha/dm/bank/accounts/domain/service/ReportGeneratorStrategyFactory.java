package com.pichincha.dm.bank.accounts.domain.service;

import com.pichincha.dm.bank.accounts.domain.Report;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReportGeneratorStrategyFactory {

    private final List<ReportGeneratorStrategy> strategies;

    public Mono<String> generateReport(Report report, String format) {
        return getStrategy(format).flatMap(strategy -> strategy.generateReport(report));
    }

    private Mono<ReportGeneratorStrategy> getStrategy(String format) {
        Map<String, ReportGeneratorStrategy> strategyMap =
                strategies.stream()
                        .collect(
                                Collectors.toMap(
                                        ReportGeneratorStrategy::getFormatType,
                                        Function.identity(),
                                        (existing, replacement) -> existing));

        ReportGeneratorStrategy strategy = strategyMap.get(format.toLowerCase());

        if (strategy == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Formato no soportado: "
                                    + format
                                    + ". Formatos disponibles: "
                                    + strategyMap.keySet()));
        }

        return Mono.just(strategy);
    }
}
