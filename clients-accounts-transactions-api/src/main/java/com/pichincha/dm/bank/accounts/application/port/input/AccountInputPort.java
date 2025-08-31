package com.pichincha.dm.bank.accounts.application.port.input;

import com.pichincha.dm.bank.accounts.domain.Account;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountInputPort {

    Mono<Account> createAccount(Account account);

    Mono<Account> getAccountByNumber(String accountNumber);

    Flux<Account> listAccounts(UUID clientId, Integer page, Integer size);

    Mono<Account> updateAccount(Account account);

    Mono<Void> deleteAccount(String accountNumber);
}
