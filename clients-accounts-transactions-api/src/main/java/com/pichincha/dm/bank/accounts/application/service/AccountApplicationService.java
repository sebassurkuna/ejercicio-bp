package com.pichincha.dm.bank.accounts.application.service;

import static com.pichincha.dm.bank.accounts.util.AccountNumberGenerator.generateAccountNumberAsLong;

import com.pichincha.dm.bank.accounts.application.port.input.AccountInputPort;
import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.exception.DataAccessException;
import com.pichincha.dm.bank.accounts.domain.exception.DataModifyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountApplicationService implements AccountInputPort {

    private final AccountOutputPort accountOutputPort;

    @Override
    public Mono<Account> createAccount(Account account) {
        account.setState(true);
        account.setAccountNumber(generateAccountNumberAsLong());
        account.setCurrentBalance(account.getInitBalance());
        return accountOutputPort.save(account).onErrorMap(DataModifyException::new);
    }

    @Override
    public Mono<Account> getAccountByNumber(String accountNumber) {
        return accountOutputPort
                .findByAccountNumber(accountNumber)
                .onErrorMap(DataAccessException::new);
    }

    @Override
    public Flux<Account> listAccounts(UUID clientId, Integer page, Integer size) {
        return accountOutputPort.findAll(clientId, page, size).onErrorMap(DataAccessException::new);
    }

    @Override
    public Mono<Account> updateAccount(Account account) {
        return accountOutputPort.update(account).onErrorMap(DataModifyException::new);
    }

    @Override
    public Mono<Void> deleteAccount(String accountNumber) {
        return accountOutputPort
                .deleteByAccountNumber(accountNumber)
                .onErrorMap(DataModifyException::new);
    }
}
