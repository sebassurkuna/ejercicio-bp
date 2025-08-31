package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.mapper;

import com.pichincha.dm.bank.accounts.domain.Report;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.AccountSummaryDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.MovimientoDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ReportDto;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ReportWithPdfBase64Dto;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "cuentas", source = "accounts")
    @Mapping(target = "clienteId", source = "clientId")
    @Mapping(target = "clienteNombre", source = "clientName")
    @Mapping(target = "fechaDesde", source = "startDate")
    @Mapping(target = "fechaHasta", source = "endDate")
    @Mapping(target = "totalDebitos", source = "totalDebits")
    @Mapping(target = "totalCreditos", source = "totalCredits")
    ReportDto toDto(Report report);

    @Mapping(target = "numeroCuenta", source = "accountNumber")
    @Mapping(target = "tipo", source = "accountType")
    @Mapping(target = "saldoInicial", source = "initialBalance")
    @Mapping(target = "saldoActual", source = "currentBalance")
    @Mapping(target = "totalDebitos", source = "totalDebits")
    @Mapping(target = "totalCreditos", source = "totalCredits")
    @Mapping(target = "movimientos", source = "movements")
    AccountSummaryDto toAccountSummaryDto(Report.AccountSummary accountSummary);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cuentaId", ignore = true) // No tenemos cuentaId en MovementSummary
    @Mapping(
            target = "fecha",
            expression = "java(localDateToOffsetDateTime(movementSummary.getDate()))")
    @Mapping(target = "tipo", source = "type")
    @Mapping(target = "valor", source = "amount")
    @Mapping(target = "saldoPostMovimiento", source = "balanceAfterTransaction")
    @Mapping(target = "createdAt", ignore = true) // No tenemos createdAt en MovementSummary
    MovimientoDto toMovimientoDto(Report.MovementSummary movementSummary);

    default OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    default ReportWithPdfBase64Dto toReportWithPdfBase64Dto(Report report, String pdfBase64) {
        ReportWithPdfBase64Dto dto = new ReportWithPdfBase64Dto();

        // Map from Report to ReportDto fields
        dto.setClienteId(report.getClientId());
        dto.setClienteNombre(report.getClientName());
        dto.setFechaDesde(report.getStartDate());
        dto.setFechaHasta(report.getEndDate());
        dto.setTotalDebitos(report.getTotalDebits());
        dto.setTotalCreditos(report.getTotalCredits());

        // Map accounts
        if (report.getAccounts() != null) {
            dto.setCuentas(report.getAccounts().stream().map(this::toAccountSummaryDto).toList());
        }

        // Set PDF base64 (convert from String to byte array)
        if (pdfBase64 != null) {
            dto.setPdfBase64(Base64.getDecoder().decode(pdfBase64));
        }

        return dto;
    }
}
