package com.pichincha.dm.bank.accounts.domain.service;

import com.pichincha.dm.bank.accounts.domain.Report;
import reactor.core.publisher.Mono;

public interface ReportGeneratorStrategy {

    Mono<String> generateReport(Report report);

    String getFormatType();
}
