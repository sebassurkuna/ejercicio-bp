package com.pichincha.dm.bank.accounts.domain.validation.impl;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.domain.exception.TransactionNotAllowedException;
import com.pichincha.dm.bank.accounts.domain.validation.AbstractMovementValidation;
import java.math.BigDecimal;

public class InsufficientBalanceValidation extends AbstractMovementValidation {

    private static final String NOT_BALANCE_MESSAGE =
            "No tienes saldo suficiente para realizar esta transacción";

    @Override
    protected void doValidate(Account account, Movement movement) {
        if (movement.getType() == MovementType.DEBITO) {
            BigDecimal newBalance = account.getCurrentBalance().subtract(movement.getValue());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new TransactionNotAllowedException(NOT_BALANCE_MESSAGE);
            }
        }
    }
}
