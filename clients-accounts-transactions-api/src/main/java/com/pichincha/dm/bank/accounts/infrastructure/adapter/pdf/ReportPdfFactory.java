package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import com.pichincha.dm.bank.accounts.domain.Report;

public class ReportPdfFactory {

    public static String createAccountStatementPdf(Report report) {
        PdfDocumentBuilder builder = new PdfDocumentBuilder();

        builder.addBankHeader("BANCO DEL PICHINCHA", "Estado de Cuenta");

        builder.addSection("Información del Cliente")
                .addInfoTable()
                .addInfoRow("Cliente:", report.getClientName())
                .addInfoRow(
                        "Período:",
                        PdfDocumentBuilder.formatDateRange(
                                report.getStartDate(), report.getEndDate()))
                .closeTable()
                .closeSection();

        builder.addSection("Resumen de Cuentas");

        if (report.getAccounts() != null && !report.getAccounts().isEmpty()) {
            builder.addDataTable("Número de Cuenta", "Tipo", "Saldo Inicial", "Saldo Actual");

            for (Report.AccountSummary account : report.getAccounts()) {
                builder.addAmountRow(
                        String.valueOf(account.getAccountNumber()),
                        account.getAccountType(),
                        PdfDocumentBuilder.formatMoney(account.getInitialBalance()),
                        PdfDocumentBuilder.formatMoney(account.getCurrentBalance()));
            }
            builder.closeTable();
        } else {
            builder.addEmptyMessage("No hay cuentas para mostrar");
        }
        builder.closeSection();

        builder.addSection("Movimientos del Período");

        if (report.getAccounts() != null && !report.getAccounts().isEmpty()) {
            boolean hasMovements = false;

            for (Report.AccountSummary account : report.getAccounts()) {
                if (account.getMovements() != null && !account.getMovements().isEmpty()) {
                    hasMovements = true;

                    builder.addSubsection("Cuenta: " + account.getAccountNumber())
                            .addDataTable("Fecha", "Tipo", "Movimiento", "Saldo Después");

                    for (Report.MovementSummary movement : account.getMovements()) {
                        boolean isPositive =
                                movement.getAmount().compareTo(java.math.BigDecimal.ZERO) >= 0;

                        builder.addDataRow(
                                PdfDocumentBuilder.formatDate(movement.getDate()),
                                movement.getType(),
                                formatMovementAmount(movement.getAmount(), isPositive),
                                PdfDocumentBuilder.formatMoney(
                                        movement.getBalanceAfterTransaction()));
                    }
                    builder.closeTable();
                }
            }

            if (!hasMovements) {
                builder.addEmptyMessage(
                        "No hay movimientos para mostrar en el período seleccionado");
            }
        } else {
            builder.addEmptyMessage("No hay movimientos para mostrar");
        }
        builder.closeSection();

        builder.addSection("Resumen del Período")
                .addSummaryTable()
                .addColoredAmountRow(
                        "Total Débitos:",
                        PdfDocumentBuilder.formatMoney(report.getTotalDebits()),
                        false)
                .addColoredAmountRow(
                        "Total Créditos:",
                        PdfDocumentBuilder.formatMoney(report.getTotalCredits()),
                        true)
                .closeTable()
                .closeSection();

        return builder.build();
    }

    public static String createAccountSummaryPdf(Report report) {
        PdfDocumentBuilder builder = new PdfDocumentBuilder();

        builder.addBankHeader("BANCO DEL PICHINCHA", "Resumen de Cuentas")
                .addSection("Información del Cliente")
                .addInfoTable()
                .addInfoRow("Cliente:", report.getClientName())
                .addInfoRow(
                        "Período:",
                        PdfDocumentBuilder.formatDateRange(
                                report.getStartDate(), report.getEndDate()))
                .closeTable()
                .closeSection()
                .addSection("Resumen de Cuentas");

        if (report.getAccounts() != null && !report.getAccounts().isEmpty()) {
            builder.addDataTable("Número de Cuenta", "Tipo", "Saldo Actual");

            for (Report.AccountSummary account : report.getAccounts()) {
                builder.addAmountRow(
                        String.valueOf(account.getAccountNumber()),
                        account.getAccountType(),
                        PdfDocumentBuilder.formatMoney(account.getCurrentBalance()));
            }
            builder.closeTable();
        } else {
            builder.addEmptyMessage("No hay cuentas para mostrar");
        }

        builder.closeSection()
                .addSection("Resumen del Período")
                .addSummaryTable()
                .addColoredAmountRow(
                        "Total Débitos:",
                        PdfDocumentBuilder.formatMoney(report.getTotalDebits()),
                        false)
                .addColoredAmountRow(
                        "Total Créditos:",
                        PdfDocumentBuilder.formatMoney(report.getTotalCredits()),
                        true)
                .closeTable()
                .closeSection();

        return builder.build();
    }

    private static String formatMovementAmount(java.math.BigDecimal amount, boolean isPositive) {
        String formatted = PdfDocumentBuilder.formatMoney(amount);
        String colorClass = isPositive ? "positive" : "negative";
        return "<span class='" + colorClass + "'>" + formatted + "</span>";
    }
}
