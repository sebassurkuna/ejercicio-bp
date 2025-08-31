package com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.interceptor;

import com.pichincha.dm.bank.accounts.domain.exception.GeneralApplicationException;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.rest.dto.ErrorDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralApplicationException.class)
    public ResponseEntity<ErrorDto> handleGeneralApplicationException(
            GeneralApplicationException ex) {
        ErrorDto errorDto =
                new ErrorDto(
                        OffsetDateTime.now(),
                        ex.getStatusCode().value(),
                        ex.getMessage(),
                        ex.getBusinessMessage());
        errorDto.setCode(ex.getStatusCode().toString());
        errorDto.setDetails(List.of());
        return ResponseEntity.status(ex.getStatusCode()).body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        ErrorDto errorDto =
                new ErrorDto(
                        OffsetDateTime.now(),
                        500,
                        ex.getLocalizedMessage(),
                        "An unexpected error occurred");
        errorDto.setCode(HttpStatusCode.valueOf(500).toString());
        errorDto.setDetails(List.of());
        return ResponseEntity.status(500).body(errorDto);
    }
}
