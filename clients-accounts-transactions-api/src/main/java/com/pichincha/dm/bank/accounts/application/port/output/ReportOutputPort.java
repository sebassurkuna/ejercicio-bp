package com.pichincha.dm.bank.accounts.application.port.output;

import com.pichincha.dm.bank.accounts.domain.Report;
import reactor.core.publisher.Mono;

public interface ReportOutputPort {

    Mono<String> generatePdfFromReport(Report report);
}
