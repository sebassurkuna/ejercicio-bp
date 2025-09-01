package com.pichincha.dm.bank.accounts.application.validation;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;

public interface MovementValidationStrategy {
    void validate(Account account, Movement movement);

    void setNext(MovementValidationStrategy next);
}
