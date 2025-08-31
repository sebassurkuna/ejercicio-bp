package com.pichincha.dm.bank.accounts.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class GeneralApplicationException extends RuntimeException {
    private final String businessMessage;
    private final HttpStatusCode statusCode;

    public GeneralApplicationException(
            String message, String businessMessage, HttpStatusCode statusCode) {
        super(message);
        this.businessMessage = businessMessage;
        this.statusCode = statusCode;
    }
}
