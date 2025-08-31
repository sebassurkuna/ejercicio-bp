package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PdfDocumentBuilder {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMATTER =
            NumberFormat.getCurrencyInstance(new Locale("es", "EC"));

    private final StringBuilder content;

    public PdfDocumentBuilder() {
        this.content = new StringBuilder();
        initializeDocument();
    }

    private void initializeDocument() {
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset=\"UTF-8\"/>");
        content.append("<title>Estado de Cuenta</title>");
        addDefaultStyles();
        content.append("</head>");
        content.append("<body>");
    }

    private void addDefaultStyles() {
        content.append("<style>");
        content.append(
                """
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
                font-size: 12px;
                color: #0F255E;
            }
            .header {
                text-align: center;
                margin-bottom: 30px;
                padding-bottom: 20px;
                border-bottom: 3px solid #FFDF00;
                background: linear-gradient(to right, #f8f9fa, #f8f9fa);
                padding: 20px;
                border-radius: 8px 8px 0 0;
            }
            h1 {
                color: #0F255E;
                text-align: center;
                margin-bottom: 10px;
                font-size: 22px;
                font-weight: bold;
            }
            h2 {
                color: #0F255E;
                text-align: center;
                margin-bottom: 20px;
                font-size: 16px;
                opacity: 0.8;
            }
            h3 {
                color: #0F255E;
                margin-top: 20px;
                margin-bottom: 10px;
                font-size: 14px;
                border-bottom: 2px solid #FFDF00;
                padding-bottom: 5px;
                font-weight: bold;
            }
            h4 {
                color: #666;
                font-size: 12px;
                margin: 15px 0 5px 0;
                font-weight: bold;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 10px 0 15px 0;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
                font-size: 11px;
            }
            th {
                background-color: #FFDF00;
                color: #0F255E;
                font-weight: bold;
                text-transform: uppercase;
                font-size: 10px;
                letter-spacing: 0.5px;
            }
            .amount {
                text-align: right;
                font-weight: bold;
            }
            .positive {
                color: #4CAF50;
            }
            .negative {
                color: #f44336;
            }
            .client-info td:first-child,
            .summary-info td:first-child {
                background-color: #FFDF00;
                color: #0F255E;
                font-weight: bold;
                width: 150px;
            }
            .client-info td:last-child,
            .summary-info td:last-child {
                background-color: #f9f9f9;
            }
            .section {
                margin-bottom: 25px;
                border-radius: 6px;
                overflow: hidden;
            }
            tr:nth-child(even) {
                background-color: #f8f9fa;
            }
            tr:hover {
                background-color: #fff8dc;
            }
            """);
        content.append("</style>");
    }

    public PdfDocumentBuilder addBankHeader(String bankName, String documentTitle) {
        content.append("<div class='header'>");
        content.append("<h1>").append(bankName).append("</h1>");
        content.append("<h2>").append(documentTitle).append("</h2>");
        content.append("</div>");
        return this;
    }

    public PdfDocumentBuilder addSection(String title) {
        content.append("<div class='section'>");
        content.append("<h3>").append(title).append("</h3>");
        return this;
    }

    public PdfDocumentBuilder closeSection() {
        content.append("</div>");
        return this;
    }

    public PdfDocumentBuilder addSubsection(String title) {
        content.append("<h4>").append(title).append("</h4>");
        return this;
    }

    public PdfDocumentBuilder addInfoTable() {
        content.append("<table class='client-info'>");
        return this;
    }

    public PdfDocumentBuilder addDataTable(String... headers) {
        content.append("<table>");
        if (headers != null && headers.length > 0) {
            content.append("<tr>");
            for (String header : headers) {
                content.append("<th>").append(header).append("</th>");
            }
            content.append("</tr>");
        }
        return this;
    }

    public PdfDocumentBuilder addSummaryTable() {
        content.append("<table class='summary-info'>");
        return this;
    }

    public PdfDocumentBuilder addInfoRow(String label, String value) {
        content.append("<tr>");
        content.append("<td><b>").append(label).append("</b></td>");
        content.append("<td>").append(value).append("</td>");
        content.append("</tr>");
        return this;
    }

    public PdfDocumentBuilder addDataRow(String... values) {
        content.append("<tr>");
        if (values != null) {
            for (String value : values) {
                content.append("<td>").append(value != null ? value : "").append("</td>");
            }
        }
        content.append("</tr>");
        return this;
    }

    public PdfDocumentBuilder addAmountRow(String... values) {
        content.append("<tr>");
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                String cssClass = (i == values.length - 1) ? "amount" : "";
                content.append("<td class='").append(cssClass).append("'>");
                content.append(values[i] != null ? values[i] : "");
                content.append("</td>");
            }
        }
        content.append("</tr>");
        return this;
    }

    public PdfDocumentBuilder addColoredAmountRow(String label, String amount, boolean isPositive) {
        content.append("<tr>");
        content.append("<td><b>").append(label).append("</b></td>");
        String colorClass = isPositive ? "amount positive" : "amount negative";
        content.append("<td class='").append(colorClass).append("'>");
        content.append(amount).append("</td>");
        content.append("</tr>");
        return this;
    }

    public PdfDocumentBuilder closeTable() {
        content.append("</table>");
        return this;
    }

    public PdfDocumentBuilder addParagraph(String text) {
        content.append("<p>").append(text).append("</p>");
        return this;
    }

    public PdfDocumentBuilder addEmptyMessage(String message) {
        content.append("<p><i>").append(message).append("</i></p>");
        return this;
    }

    public String build() {
        content.append("</body>");
        content.append("</html>");
        return content.toString();
    }

    public static String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }

    public static String formatMoney(java.math.BigDecimal amount) {
        return amount != null ? CURRENCY_FORMATTER.format(amount) : "$0.00";
    }

    public static String formatDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        return formatDate(start) + " - " + formatDate(end);
    }
}
