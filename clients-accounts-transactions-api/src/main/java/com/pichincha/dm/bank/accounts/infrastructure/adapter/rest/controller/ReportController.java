package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.controller;

import com.pichincha.dm.bank.accounts.application.port.input.ReportInputPort;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.api.ReportsApi;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.GenerateReport200Response;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.PdfReportResponseDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper.ReportMapper;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportInputPort reportInputPort;
    private final ReportMapper reportMapper;

    @Override
    public Mono<ResponseEntity<GenerateReport200Response>> generateReport(
            UUID clienteId,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String formato,
            ServerWebExchange exchange) {

        return switch (formato.toLowerCase()) {
            case "json" -> generateJsonReport(clienteId, fechaDesde, fechaHasta);
            case "pdf" -> generatePdfReport(clienteId, fechaDesde, fechaHasta);
            default -> Mono.error(
                    new IllegalArgumentException(
                            "Formato no soportado: "
                                    + formato
                                    + ". Formatos disponibles: json, pdf"));
        };
    }

    private Mono<ResponseEntity<GenerateReport200Response>> generateJsonReport(
            UUID clienteId, LocalDate fechaDesde, LocalDate fechaHasta) {
        return reportInputPort
                .generateReport(clienteId, fechaDesde, fechaHasta)
                .map(reportMapper::toDto)
                .map(ResponseEntity::ok);
    }

    private Mono<ResponseEntity<GenerateReport200Response>> generatePdfReport(
            UUID clienteId, LocalDate fechaDesde, LocalDate fechaHasta) {
        return reportInputPort
                .generateFormattedReport(clienteId, fechaDesde, fechaHasta, "pdf")
                .map(
                        pdfBase64Content -> {
                            PdfReportResponseDto pdfResponse =
                                    new PdfReportResponseDto(pdfBase64Content);
                            return ResponseEntity.ok()
                                    .header("Content-Type", "application/json")
                                    .body(pdfResponse);
                        });
    }
}
