package com.pichincha.dm.bank.accounts.application.port.input;

import com.pichincha.dm.bank.accounts.domain.Report;
import java.time.LocalDate;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ReportInputPort {

    Mono<Report> generateReport(UUID clientId, LocalDate startDate, LocalDate endDate);

    Mono<String> generateFormattedReport(
            UUID clientId, LocalDate startDate, LocalDate endDate, String format);
}
