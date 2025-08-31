package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PdfReportResponseDto implements GenerateReport200Response {

    @JsonProperty("pdfBase64")
    private String pdfBase64;

    public PdfReportResponseDto() {}

    public PdfReportResponseDto(String pdfBase64) {
        this.pdfBase64 = pdfBase64;
    }

    public String getPdfBase64() {
        return pdfBase64;
    }

    public void setPdfBase64(String pdfBase64) {
        this.pdfBase64 = pdfBase64;
    }
}
