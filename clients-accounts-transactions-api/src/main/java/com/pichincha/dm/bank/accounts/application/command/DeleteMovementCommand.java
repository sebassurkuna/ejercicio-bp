package com.pichincha.dm.bank.accounts.application.command;

import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.application.service.BalanceRecalculationService;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeleteMovementCommand implements MovementCommand {

    private final MovementOutputPort movementOutputPort;
    private final AccountOutputPort accountOutputPort;
    private final BalanceRecalculationService balanceRecalculationService;
    private Movement movementToDelete;
    private Account account;

    public DeleteMovementCommand withMovementToDelete(Movement movementToDelete) {
        this.movementToDelete = movementToDelete;
        return this;
    }

    public DeleteMovementCommand withAccount(Account account) {
        this.account = account;
        return this;
    }

    @Override
    public Mono<Void> execute() {
        BigDecimal newAccountBalance = calculateNewAccountBalance();

        BigDecimal balanceAdjustment = movementToDelete.getValue().negate();

        return accountOutputPort
                .updateBalance(account.getId(), newAccountBalance)
                .then(movementOutputPort.deleteById(movementToDelete.getId()))
                .then(recalculateSubsequentMovements(balanceAdjustment));
    }

    private BigDecimal calculateNewAccountBalance() {
        return account.getCurrentBalance().subtract(movementToDelete.getValue());
    }

    private Mono<Void> recalculateSubsequentMovements(BigDecimal balanceAdjustment) {
        return balanceRecalculationService.recalculatePostMovementBalances(
                account.getId(), movementToDelete.getDate().plusNanos(1), balanceAdjustment);
    }
}
