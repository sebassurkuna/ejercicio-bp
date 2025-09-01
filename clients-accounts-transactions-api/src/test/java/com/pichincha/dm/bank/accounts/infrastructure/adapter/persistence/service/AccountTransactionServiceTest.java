package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.AccountEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.AccountRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountTransactionServiceTest {

    @Mock private AccountRepository accountRepository;

    @Mock private AccountEntityMapper accountMapper;

    @InjectMocks private AccountTransactionService accountTransactionService;

    private Account testAccount;
    private AccountEntity testAccountEntity;
    private Account savedAccount;
    private AccountEntity savedAccountEntity;

    @BeforeEach
    void setUp() {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        testAccount =
                Account.builder()
                        .id(accountId)
                        .clientId(clientId)
                        .accountNumber(1001L)
                        .type(AccountType.AHORROS)
                        .initBalance(BigDecimal.valueOf(1000.00))
                        .currentBalance(BigDecimal.valueOf(1500.00))
                        .state(true)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .updatedAt(LocalDateTime.of(2024, 1, 15, 15, 30))
                        .build();

        testAccountEntity =
                new AccountEntity(
                        accountId,
                        clientId,
                        1001L,
                        AccountType.AHORROS,
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(1500.00),
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        savedAccount = Account.builder().id(UUID.randomUUID()).build();

        savedAccountEntity =
                new AccountEntity(
                        savedAccount.getId(),
                        clientId,
                        1001L,
                        AccountType.AHORROS,
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(1500.00),
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));
    }

    @Test
    void givenValidAccountWhenSaveAccountThenReturnSavedAccountMono() {
        // Arrange
        doReturn(testAccountEntity).when(accountMapper).toEntity(testAccount);
        doReturn(Mono.just(savedAccountEntity)).when(accountRepository).save(testAccountEntity);
        doReturn(savedAccount).when(accountMapper).toDomain(savedAccountEntity);

        // Act
        Mono<Account> result = accountTransactionService.saveAccount(testAccount);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        account -> {
                            assertAll(
                                    () -> assertNotNull(account),
                                    () -> assertEquals(savedAccount.getId(), account.getId()),
                                    () ->
                                            assertEquals(
                                                    savedAccount.getClientId(),
                                                    account.getClientId()),
                                    () ->
                                            assertEquals(
                                                    savedAccount.getAccountNumber(),
                                                    account.getAccountNumber()),
                                    () -> assertEquals(savedAccount.getType(), account.getType()),
                                    () ->
                                            assertEquals(
                                                    savedAccount.getInitBalance(),
                                                    account.getInitBalance()),
                                    () ->
                                            assertEquals(
                                                    savedAccount.getCurrentBalance(),
                                                    account.getCurrentBalance()),
                                    () ->
                                            assertEquals(
                                                    savedAccount.getState(), account.getState()));
                        })
                .verifyComplete();

        verify(accountMapper).toEntity(testAccount);
        verify(accountRepository).save(testAccountEntity);
        verify(accountMapper).toDomain(savedAccountEntity);
        verifyNoMoreInteractions(accountMapper, accountRepository);
    }

    @Test
    void givenValidAccountWhenUpdateAccountThenReturnUpdatedAccountMono() {
        // Arrange
        Account updatedAccountData =
                Account.builder()
                        .currentBalance(BigDecimal.valueOf(2000.00))
                        .updatedAt(LocalDateTime.of(2024, 2, 1, 12, 0))
                        .build();

        AccountEntity updatedEntity =
                new AccountEntity(
                        testAccount.getId(),
                        testAccount.getClientId(),
                        testAccount.getAccountNumber(),
                        testAccount.getType(),
                        testAccount.getInitBalance(),
                        BigDecimal.valueOf(2000.00),
                        testAccount.getState(),
                        testAccount.getCreatedAt(),
                        LocalDateTime.of(2024, 2, 1, 12, 0));

        Account updatedAccount = Account.builder().build();

        doReturn(updatedEntity).when(accountMapper).toEntity(updatedAccountData);
        doReturn(Mono.just(updatedEntity)).when(accountRepository).save(updatedEntity);
        doReturn(updatedAccount).when(accountMapper).toDomain(updatedEntity);

        // Act
        Mono<Account> result = accountTransactionService.updateAccount(updatedAccountData);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        account -> {
                            assertAll(
                                    () -> assertNotNull(account),
                                    () -> assertEquals(updatedAccount.getId(), account.getId()),
                                    () ->
                                            assertEquals(
                                                    updatedAccount.getClientId(),
                                                    account.getClientId()),
                                    () ->
                                            assertEquals(
                                                    updatedAccount.getCurrentBalance(),
                                                    account.getCurrentBalance()),
                                    () ->
                                            assertEquals(
                                                    updatedAccount.getUpdatedAt(),
                                                    account.getUpdatedAt()));
                        })
                .verifyComplete();

        verify(accountMapper).toEntity(updatedAccountData);
        verify(accountRepository).save(updatedEntity);
        verify(accountMapper).toDomain(updatedEntity);
        verifyNoMoreInteractions(accountMapper, accountRepository);
    }
}
