package com.pichincha.dm.bank.accounts.domain.exception;

import org.springframework.http.HttpStatusCode;

public class DataAccessException extends GeneralApplicationException {
    private static final String DEFAULT_BUSINESS_MESSAGE = "Data access error occurred";

    public DataAccessException(Throwable ex) {
        super(ex.getCause().getMessage(), DEFAULT_BUSINESS_MESSAGE, HttpStatusCode.valueOf(400));
    }
}
