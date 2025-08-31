package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pichincha.dm.bank.accounts.application.port.output.ReportOutputPort;
import com.pichincha.dm.bank.accounts.domain.Report;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PdfGeneratorAdapter implements ReportOutputPort {

    @Override
    public Mono<String> generatePdfFromReport(Report report) {
        return Mono.fromCallable(() -> generatePdfContent(report))
                .onErrorMap(
                        ex -> new RuntimeException("Error generating PDF: " + ex.getMessage(), ex));
    }

    private String generatePdfContent(Report report) throws Exception {
        String htmlContent = ReportPdfFactory.createAccountStatementPdf(report);
        return convertHtmlToPdfBase64(htmlContent, "Complete Account Statement");
    }

    private String convertHtmlToPdfBase64(String htmlContent, String reportType) throws Exception {
        // Log para debug
        log.info(
                "Generating {} - HTML content length: {} characters",
                reportType,
                htmlContent.length());
        log.debug(
                "HTML content preview: {}",
                htmlContent.substring(0, Math.min(500, htmlContent.length())));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);

            builder.useFastMode();
            builder.testMode(false);

            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("Generated {} PDF size: {} bytes", reportType, pdfBytes.length);

            return Base64.getEncoder().encodeToString(pdfBytes);
        }
    }
}
