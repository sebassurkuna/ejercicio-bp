package com.pichincha.dm.bank.accounts.domain.validation;

import com.pichincha.dm.bank.accounts.domain.validation.impl.InsufficientBalanceValidation;
import com.pichincha.dm.bank.accounts.domain.validation.impl.ZeroBalanceValidation;

public class MovementValidationChain {

    private final MovementValidationStrategy firstValidation;

    private MovementValidationChain(MovementValidationStrategy firstValidation) {
        this.firstValidation = firstValidation;
    }

    public static MovementValidationChain createDefaultChain() {
        ZeroBalanceValidation zeroBalanceValidation = new ZeroBalanceValidation();
        InsufficientBalanceValidation insufficientBalanceValidation =
                new InsufficientBalanceValidation();

        zeroBalanceValidation.setNext(insufficientBalanceValidation);

        return new MovementValidationChain(zeroBalanceValidation);
    }

    public static Builder builder() {
        return new Builder();
    }

    public MovementValidationStrategy getValidationChain() {
        return firstValidation;
    }

    public static class Builder {
        private MovementValidationStrategy first;
        private MovementValidationStrategy current;

        public Builder addValidation(MovementValidationStrategy validation) {
            if (first == null) {
                first = validation;
                current = validation;
            } else {
                current.setNext(validation);
                current = validation;
            }
            return this;
        }

        public MovementValidationChain build() {
            return new MovementValidationChain(first);
        }
    }
}
