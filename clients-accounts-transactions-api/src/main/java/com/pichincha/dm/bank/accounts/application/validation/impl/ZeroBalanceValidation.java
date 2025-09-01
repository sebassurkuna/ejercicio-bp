package com.pichincha.dm.bank.accounts.application.validation.impl;

import com.pichincha.dm.bank.accounts.application.validation.AbstractMovementValidation;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.domain.exception.TransactionNotAllowedException;
import java.math.BigDecimal;

public class ZeroBalanceValidation extends AbstractMovementValidation {

    private static final String INSUFFICIENT_FUNDS_MESSAGE = "Fondos insuficientes. Saldo actual: ";

    @Override
    protected void doValidate(Account account, Movement movement) {
        if (movement.getType() == MovementType.DEBITO) {
            BigDecimal currentBalance = account.getCurrentBalance();
            BigDecimal newBalance = currentBalance.add(movement.getValue());

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                String errorMessage =
                        INSUFFICIENT_FUNDS_MESSAGE
                                + currentBalance
                                + ", Monto solicitado: "
                                + movement.getValue().abs();
                throw new TransactionNotAllowedException(errorMessage);
            }
        }
    }
}
