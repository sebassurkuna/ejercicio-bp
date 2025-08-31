package com.pichincha.dm.bank.accounts.application.port.output;

import com.pichincha.dm.bank.accounts.domain.Account;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountOutputPort {

    Mono<Account> save(Account account);

    Mono<Account> findById(UUID id);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findAll(UUID clientId, Integer page, Integer size);

    Mono<Account> update(Account account);

    Mono<Account> updateBalance(UUID accountId, java.math.BigDecimal newBalance);

    Mono<Void> deleteByAccountNumber(String accountNumber);
}
