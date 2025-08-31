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
public class UpdateMovementCommand implements MovementCommand {

    private final MovementOutputPort movementOutputPort;
    private final AccountOutputPort accountOutputPort;
    private final BalanceRecalculationService balanceRecalculationService;
    private Movement oldMovement;
    private Movement newMovement;
    private Account account;

    public UpdateMovementCommand withOldMovement(Movement oldMovement) {
        this.oldMovement = oldMovement;
        return this;
    }

    public UpdateMovementCommand withNewMovement(Movement newMovement) {
        this.newMovement = newMovement;
        return this;
    }

    public UpdateMovementCommand withAccount(Account account) {
        this.account = account;
        return this;
    }

    @Override
    public Mono<Void> execute() {

        BigDecimal balanceAdjustment = calculateBalanceAdjustment();

        BigDecimal newAccountBalance = calculateNewAccountBalance();

        BigDecimal newPostMovementBalance = calculateNewPostMovementBalance();
        newMovement.setPostMovementBalance(newPostMovementBalance);

        return accountOutputPort
                .updateBalance(account.getId(), newAccountBalance)
                .then(movementOutputPort.update(newMovement))
                .then(recalculateSubsequentMovements(balanceAdjustment));
    }

    private BigDecimal calculateBalanceAdjustment() {
        return newMovement.getValue().subtract(oldMovement.getValue());
    }

    private BigDecimal calculateNewAccountBalance() {
        return account.getCurrentBalance()
                .subtract(oldMovement.getValue())
                .add(newMovement.getValue());
    }

    private BigDecimal calculateNewPostMovementBalance() {

        BigDecimal balanceBeforeMovement =
                oldMovement.getPostMovementBalance().subtract(oldMovement.getValue());
        return balanceBeforeMovement.add(newMovement.getValue());
    }

    private Mono<Void> recalculateSubsequentMovements(BigDecimal balanceAdjustment) {
        if (balanceAdjustment.compareTo(BigDecimal.ZERO) == 0) {
            return Mono.empty();
        }

        return balanceRecalculationService.recalculatePostMovementBalances(
                account.getId(), oldMovement.getDate().plusNanos(1), balanceAdjustment);
    }
}
