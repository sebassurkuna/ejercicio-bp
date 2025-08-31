package com.pichincha.dm.bank.accounts.infrastructure.adapter.report.strategy;

import com.pichincha.dm.bank.accounts.application.port.output.ReportOutputPort;
import com.pichincha.dm.bank.accounts.domain.Report;
import com.pichincha.dm.bank.accounts.domain.service.ReportGeneratorStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PdfReportGeneratorStrategy implements ReportGeneratorStrategy {

    private final ReportOutputPort reportOutputPort;

    @Override
    public Mono<String> generateReport(Report report) {
        return reportOutputPort.generatePdfFromReport(report);
    }

    @Override
    public String getFormatType() {
        return "pdf";
    }
}
