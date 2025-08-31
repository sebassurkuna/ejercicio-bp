package com.pichincha.dm.bank.accounts.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

@Slf4j
public class DataModifyException extends GeneralApplicationException {
    private static final String DEFAULT_BUSINESS_MESSAGE = "Data modification error occurred";

    public DataModifyException(Throwable ex) {
        super(ex.getMessage(), DEFAULT_BUSINESS_MESSAGE, HttpStatusCode.valueOf(400));
        log.error("DataModifyException: ", ex);
    }
}
