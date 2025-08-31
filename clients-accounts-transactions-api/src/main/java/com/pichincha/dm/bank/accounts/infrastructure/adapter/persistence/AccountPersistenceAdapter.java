package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.AccountEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.AccountRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.AccountTransactionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountOutputPort {

    private final AccountRepository accountRepository;
    private final AccountEntityMapper accountMapper;
    private final AccountTransactionService transactionService;

    @Override
    public Mono<Account> save(Account account) {
        return transactionService
                .saveAccount(account)
                .flatMap(accountSaved -> findById(accountSaved.getId()));
    }

    @Override
    public Mono<Account> findById(UUID id) {
        return accountRepository.findById(id).map(accountMapper::toDomain);
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return accountRepository
                .findByAccountNumber(Long.parseLong(accountNumber))
                .map(accountMapper::toDomain);
    }

    @Override
    public Flux<Account> findAll(UUID clientId, Integer page, Integer size) {
        int pageSize = size != null ? size : 20;
        int offset = page != null ? page * pageSize : 0;

        return accountRepository
                .findWithFilters(clientId, pageSize, offset)
                .map(accountMapper::toDomain);
    }

    @Override
    public Mono<Account> update(Account account) {
        account.setUpdatedAt(LocalDateTime.now());
        return transactionService.updateAccount(account);
    }

    @Override
    public Mono<Account> updateBalance(UUID accountId, BigDecimal newBalance) {
        return findById(accountId)
                .flatMap(
                        account -> {
                            account.setCurrentBalance(newBalance);
                            return transactionService.updateAccount(account);
                        });
    }

    @Override
    public Mono<Void> deleteByAccountNumber(String accountNumber) {
        return accountRepository.deleteByAccountNumber(Long.valueOf(accountNumber));
    }
}
