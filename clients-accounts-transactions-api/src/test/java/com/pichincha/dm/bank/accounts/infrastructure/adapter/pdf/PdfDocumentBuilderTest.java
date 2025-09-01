package com.pichincha.dm.bank.accounts.infrastructure.adapter.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PdfDocumentBuilderTest {

    private PdfDocumentBuilder pdfDocumentBuilder;

    @BeforeEach
    void setUp() {
        pdfDocumentBuilder = new PdfDocumentBuilder();
    }

    @Test
    void givenNewBuilderWhenConstructorCalledThenInitializeDocumentCorrectly() {
        // Arrange & Act
        PdfDocumentBuilder builder = new PdfDocumentBuilder();
        String result = builder.build();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("<!DOCTYPE html>"));
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("<head>"));
        assertTrue(result.contains("<meta charset=\"UTF-8\"/>"));
        assertTrue(result.contains("<title>Estado de Cuenta</title>"));
        assertTrue(result.contains("<style>"));
        assertTrue(result.contains("</head>"));
        assertTrue(result.contains("<body>"));
        assertTrue(result.contains("</body>"));
        assertTrue(result.contains("</html>"));
    }

    @Test
    void givenBankHeaderDataWhenAddBankHeaderThenHeaderIsAdded() {
        // Arrange
        String bankName = "Banco Pichincha";
        String documentTitle = "Estado de Cuenta Mensual";

        // Act
        String result = pdfDocumentBuilder.addBankHeader(bankName, documentTitle).build();

        // Assert
        assertTrue(result.contains("<div class='header'>"));
        assertTrue(result.contains("<h1>Banco Pichincha</h1>"));
        assertTrue(result.contains("<h2>Estado de Cuenta Mensual</h2>"));
        assertTrue(result.contains("</div>"));
    }

    @Test
    void givenSectionTitleWhenAddSectionThenSectionIsAdded() {
        // Arrange
        String sectionTitle = "Información del Cliente";

        // Act
        String result = pdfDocumentBuilder.addSection(sectionTitle).closeSection().build();

        // Assert
        assertTrue(result.contains("<div class='section'>"));
        assertTrue(result.contains("<h3>Información del Cliente</h3>"));
        assertTrue(result.contains("</div>"));
    }

    @Test
    void givenSubsectionTitleWhenAddSubsectionThenSubsectionIsAdded() {
        // Arrange
        String subsectionTitle = "Datos Personales";

        // Act
        String result = pdfDocumentBuilder.addSubsection(subsectionTitle).build();

        // Assert
        assertTrue(result.contains("<h4>Datos Personales</h4>"));
    }

    @Test
    void givenInfoTableWhenAddInfoTableThenInfoTableIsAdded() {
        // Arrange & Act
        String result = pdfDocumentBuilder.addInfoTable().closeTable().build();

        // Assert
        assertTrue(result.contains("<table class='client-info'>"));
        assertTrue(result.contains("</table>"));
    }

    @Test
    void givenHeadersWhenAddDataTableThenDataTableWithHeadersIsAdded() {
        // Arrange
        String[] headers = {"Fecha", "Descripción", "Débito", "Crédito", "Saldo"};

        // Act
        String result = pdfDocumentBuilder.addDataTable(headers).closeTable().build();

        // Assert
        assertTrue(result.contains("<table>"));
        assertTrue(result.contains("<tr>"));
        assertTrue(result.contains("<th>Fecha</th>"));
        assertTrue(result.contains("<th>Descripción</th>"));
        assertTrue(result.contains("<th>Débito</th>"));
        assertTrue(result.contains("<th>Crédito</th>"));
        assertTrue(result.contains("<th>Saldo</th>"));
        assertTrue(result.contains("</tr>"));
        assertTrue(result.contains("</table>"));
    }

    @Test
    void givenSummaryTableWhenAddSummaryTableThenSummaryTableIsAdded() {
        // Arrange & Act
        String result = pdfDocumentBuilder.addSummaryTable().closeTable().build();

        // Assert
        assertTrue(result.contains("<table class='summary-info'>"));
        assertTrue(result.contains("</table>"));
    }

    @Test
    void givenLabelAndValueWhenAddInfoRowThenInfoRowIsAdded() {
        // Arrange
        String label = "Nombre";
        String value = "Juan Pérez";

        // Act
        String result =
                pdfDocumentBuilder.addInfoTable().addInfoRow(label, value).closeTable().build();

        // Assert
        assertTrue(result.contains("<tr>"));
        assertTrue(result.contains("<td><b>Nombre</b></td>"));
        assertTrue(result.contains("<td>Juan Pérez</td>"));
        assertTrue(result.contains("</tr>"));
    }

    @Test
    void givenValuesWhenAddDataRowThenDataRowIsAdded() {
        // Arrange
        String[] values = {"01/01/2024", "Depósito inicial", "", "$1,000.00", "$1,000.00"};

        // Act
        String result = pdfDocumentBuilder.addDataTable().addDataRow(values).closeTable().build();

        // Assert
        assertTrue(result.contains("<tr>"));
        assertTrue(result.contains("<td>01/01/2024</td>"));
        assertTrue(result.contains("<td>Depósito inicial</td>"));
        assertTrue(result.contains("<td></td>"));
        assertTrue(result.contains("<td>$1,000.00</td>"));
        assertTrue(result.contains("</tr>"));
    }

    @Test
    void givenAmountValuesWhenAddAmountRowThenAmountRowIsAdded() {
        // Arrange
        String[] values = {"Total Débitos", "$500.00"};

        // Act
        String result = pdfDocumentBuilder.addDataTable().addAmountRow(values).closeTable().build();

        // Assert
        assertTrue(result.contains("<tr>"));
        assertTrue(result.contains("<td class=''>Total Débitos</td>"));
        assertTrue(result.contains("<td class='amount'>$500.00</td>"));
        assertTrue(result.contains("</tr>"));
    }

    @Test
    void givenPositiveAmountWhenAddColoredAmountRowThenPositiveAmountRowIsAdded() {
        // Arrange
        String label = "Total Créditos";
        String amount = "$2,000.00";
        boolean isPositive = true;

        // Act
        String result =
                pdfDocumentBuilder
                        .addSummaryTable()
                        .addColoredAmountRow(label, amount, isPositive)
                        .closeTable()
                        .build();

        // Assert
        assertTrue(result.contains("<tr>"));
        assertTrue(result.contains("<td><b>Total Créditos</b></td>"));
        assertTrue(result.contains("<td class='amount positive'>$2,000.00</td>"));
        assertTrue(result.contains("</tr>"));
    }

    @Test
    void givenParagraphTextWhenAddParagraphThenParagraphIsAdded() {
        // Arrange
        String text = "Este documento contiene información confidencial del cliente.";

        // Act
        String result = pdfDocumentBuilder.addParagraph(text).build();

        // Assert
        assertTrue(
                result.contains(
                        "<p>Este documento contiene información confidencial del cliente.</p>"));
    }

    @Test
    void givenEmptyMessageWhenAddEmptyMessageThenEmptyMessageIsAdded() {
        // Arrange
        String message = "No hay movimientos en el período seleccionado.";

        // Act
        String result = pdfDocumentBuilder.addEmptyMessage(message).build();

        // Assert
        assertTrue(result.contains("<p><i>No hay movimientos en el período seleccionado.</i></p>"));
    }

    @Test
    void givenValidLocalDateWhenFormatDateThenFormattedDateIsReturned() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 15);

        // Act
        String result = PdfDocumentBuilder.formatDate(date);

        // Assert
        assertEquals("15/01/2024", result);
    }

    @Test
    void givenValidLocalDateTimeWhenFormatDateTimeThenFormattedDateIsReturned() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 20, 14, 30, 45);

        // Act
        String result = PdfDocumentBuilder.formatDateTime(dateTime);

        // Assert
        assertEquals("20/03/2024", result);
    }

    @Test
    void givenValidBigDecimalWhenFormatMoneyThenFormattedMoneyIsReturned() {
        // Arrange
        BigDecimal amount = new BigDecimal("1500.75");

        // Act
        String result = PdfDocumentBuilder.formatMoney(amount);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("1500") || result.contains("1.500"));
        assertTrue(result.contains("75"));
    }

    @Test
    void givenDateRangeWhenFormatDateRangeThenFormattedDateRangeIsReturned() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // Act
        String result = PdfDocumentBuilder.formatDateRange(startDate, endDate);

        // Assert
        assertEquals("01/01/2024 - 31/01/2024", result);
    }
}
