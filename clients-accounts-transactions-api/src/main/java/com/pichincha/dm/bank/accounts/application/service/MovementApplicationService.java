package com.pichincha.dm.bank.accounts.application.service;

import com.pichincha.dm.bank.accounts.application.command.DeleteMovementCommand;
import com.pichincha.dm.bank.accounts.application.command.UpdateMovementCommand;
import com.pichincha.dm.bank.accounts.application.port.input.MovementInputPort;
import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.application.port.output.MovementOutputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.enums.MovementType;
import com.pichincha.dm.bank.accounts.domain.exception.DataAccessException;
import com.pichincha.dm.bank.accounts.domain.exception.DataModifyException;
import com.pichincha.dm.bank.accounts.domain.exception.TransactionNotAllowedException;
import com.pichincha.dm.bank.accounts.domain.validation.MovementValidationChain;
import com.pichincha.dm.bank.accounts.domain.validation.MovementValidationStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementApplicationService implements MovementInputPort {

    private final MovementOutputPort movementOutputPort;
    private final AccountOutputPort accountOutputPort;
    private final MovementValidationStrategy validationChain =
            MovementValidationChain.createDefaultChain().getValidationChain();

    private final UpdateMovementCommand updateMovementCommand;

    private final DeleteMovementCommand deleteMovementCommand;

    private static final String INVALID_CREDIT_VALUE_MESSAGE =
            "Los créditos deben tener valores positivos";

    private static final String INVALID_DEBIT_VALUE_MESSAGE =
            "Los débitos deben tener valores negativos";

    //    public MovementApplicationService(
    //            MovementOutputPort movementOutputPort,
    //            AccountOutputPort accountOutputPort,
    //            BalanceRecalculationService balanceRecalculationService) {
    //        this.movementOutputPort = movementOutputPort;
    //        this.accountOutputPort = accountOutputPort;
    //        this.balanceRecalculationService = balanceRecalculationService;
    //        this.validationChain =
    // MovementValidationChain.createDefaultChain().getValidationChain();
    //    }

    @Override
    public Mono<Movement> createMovement(Movement movement) {
        return accountOutputPort
                .findById(movement.getAccountId())
                .flatMap(account -> processMovement(account, movement))
                .onErrorMap(
                        throwable -> {
                            if (throwable instanceof TransactionNotAllowedException) {
                                return throwable;
                            }
                            return new DataModifyException(throwable);
                        });
    }

    private Mono<Movement> processMovement(Account account, Movement movement) {

        if (movement.getType() == MovementType.CREDITO
                && movement.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new TransactionNotAllowedException(INVALID_CREDIT_VALUE_MESSAGE));
        }

        if (movement.getType() == MovementType.DEBITO
                && movement.getValue().compareTo(BigDecimal.ZERO) >= 0) {
            return Mono.error(new TransactionNotAllowedException(INVALID_DEBIT_VALUE_MESSAGE));
        }

        validateMovement(account, movement);

        BigDecimal newBalance =
                calculateNewBalance(account.getCurrentBalance(), movement.getValue());
        Movement enrichedMovement = enrichMovementWithTimestamps(movement, newBalance);

        return executeTransaction(account, enrichedMovement, newBalance);
    }

    private void validateMovement(Account account, Movement movement) {
        validationChain.validate(account, movement);
    }

    private Movement enrichMovementWithTimestamps(Movement movement, BigDecimal newBalance) {
        movement.setDate(LocalDateTime.now());
        movement.setPostMovementBalance(newBalance);
        movement.setCreatedAt(LocalDateTime.now());
        return movement;
    }

    private Mono<Movement> executeTransaction(
            Account account, Movement movement, BigDecimal newBalance) {
        return accountOutputPort
                .updateBalance(account.getId(), newBalance)
                .then(movementOutputPort.save(movement));
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal value) {
        return currentBalance.add(value);
    }

    @Override
    public Mono<Movement> getMovementById(UUID id) {
        return movementOutputPort.findById(id).onErrorMap(DataAccessException::new);
    }

    @Override
    public Flux<Movement> listMovements(
            UUID clientId,
            Long accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        return movementOutputPort
                .findAll(clientId, accountNumber, startDate, endDate, page, size)
                .onErrorMap(DataAccessException::new);
    }

    @Override
    public Mono<Movement> updateMovement(Movement movement) {
        return accountOutputPort
                .findById(movement.getAccountId())
                .zipWith(movementOutputPort.findById(movement.getId()))
                .flatMap(
                        tuple -> {
                            Account account = tuple.getT1();
                            Movement existingMovement = tuple.getT2();

                            updateMovementCommand
                                    .withNewMovement(movement)
                                    .withOldMovement(existingMovement)
                                    .withAccount(account);

                            return updateMovementCommand.execute().thenReturn(movement);
                        })
                .onErrorMap(DataModifyException::new);
    }

    @Override
    public Mono<Void> deleteMovement(UUID id) {
        return movementOutputPort
                .findById(id)
                .flatMap(
                        movementToDelete ->
                                accountOutputPort
                                        .findById(movementToDelete.getAccountId())
                                        .map(
                                                account ->
                                                        deleteMovementCommand
                                                                .withMovementToDelete(
                                                                        movementToDelete)
                                                                .withAccount(account)))
                .flatMap(DeleteMovementCommand::execute)
                .onErrorMap(DataModifyException::new);
    }
}
