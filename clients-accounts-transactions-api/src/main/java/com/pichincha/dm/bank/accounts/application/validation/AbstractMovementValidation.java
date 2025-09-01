package com.pichincha.dm.bank.accounts.application.validation;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;

public abstract class AbstractMovementValidation implements MovementValidationStrategy {

    private MovementValidationStrategy next;

    @Override
    public void setNext(MovementValidationStrategy next) {
        this.next = next;
    }

    @Override
    public void validate(Account account, Movement movement) {
        doValidate(account, movement);
        if (next != null) {
            next.validate(account, movement);
        }
    }

    protected abstract void doValidate(Account account, Movement movement);
}
