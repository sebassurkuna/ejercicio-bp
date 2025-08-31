package com.pichincha.dm.bank.accounts.domain.exception;

import org.springframework.http.HttpStatusCode;

public class TransactionNotAllowedException extends GeneralApplicationException {

    private static final HttpStatusCode STATUS_CODE = HttpStatusCode.valueOf(403);
    private static final String BUSINESS_MESSAGE = "Transaction not allowed";

    public TransactionNotAllowedException(String message) {
        super(BUSINESS_MESSAGE, message, STATUS_CODE);
    }
}
