package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.AccountEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final AccountEntityMapper accountMapper;

    @Transactional
    public Mono<Account> saveAccount(Account account) {
        AccountEntity entity = accountMapper.toEntity(account);
        return accountRepository.save(entity).map(accountMapper::toDomain);
    }

    @Transactional
    public Mono<Account> updateAccount(Account account) {
        AccountEntity entity = accountMapper.toEntity(account);
        return accountRepository.save(entity).map(accountMapper::toDomain);
    }
}
