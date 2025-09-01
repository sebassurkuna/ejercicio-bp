package com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.mapper.AccountEntityMapper;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.repository.AccountRepository;
import com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence.service.AccountTransactionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceAdapterTest {

    @Mock private AccountRepository accountRepository;

    @Mock private AccountEntityMapper accountMapper;

    @Mock private AccountTransactionService transactionService;

    @InjectMocks private AccountPersistenceAdapter accountPersistenceAdapter;

    private Account testAccount;
    private AccountEntity testAccountEntity;
    private UUID testAccountId;
    private UUID testClientId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testClientId = UUID.randomUUID();

        testAccount =
                Account.builder()
                        .id(testAccountId)
                        .clientId(testClientId)
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
                        testAccountId,
                        testClientId,
                        1001L,
                        AccountType.AHORROS,
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(1500.00),
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));
    }

    @Test
    void givenValidAccountWhenSaveThenReturnSavedAccountMono() {
        // Arrange
        Account savedAccount = Account.builder().id(UUID.randomUUID()).build();

        doReturn(Mono.just(savedAccount)).when(transactionService).saveAccount(testAccount);
        doReturn(Mono.just(testAccountEntity))
                .when(accountRepository)
                .findById(savedAccount.getId());
        doReturn(savedAccount).when(accountMapper).toDomain(testAccountEntity);

        // Act
        Mono<Account> result = accountPersistenceAdapter.save(testAccount);

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
                                                    savedAccount.getCurrentBalance(),
                                                    account.getCurrentBalance()));
                        })
                .verifyComplete();

        verify(transactionService).saveAccount(testAccount);
        verify(accountRepository).findById(savedAccount.getId());
        verify(accountMapper).toDomain(testAccountEntity);
    }

    @Test
    void givenValidAccountNumberWhenFindByAccountNumberThenReturnAccountMono() {
        // Arrange
        String accountNumber = "1001";
        Long accountNumberLong = 1001L;

        doReturn(Mono.just(testAccountEntity))
                .when(accountRepository)
                .findByAccountNumber(accountNumberLong);
        doReturn(testAccount).when(accountMapper).toDomain(testAccountEntity);

        // Act
        Mono<Account> result = accountPersistenceAdapter.findByAccountNumber(accountNumber);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        account -> {
                            assertAll(
                                    () -> assertNotNull(account),
                                    () ->
                                            assertEquals(
                                                    testAccount.getAccountNumber(),
                                                    account.getAccountNumber()),
                                    () -> assertEquals(testAccount.getId(), account.getId()));
                        })
                .verifyComplete();

        verify(accountRepository).findByAccountNumber(accountNumberLong);
        verify(accountMapper).toDomain(testAccountEntity);
    }

    @Test
    void givenValidFiltersWhenFindAllThenReturnAccountsFlux() {
        // Arrange
        Integer page = 0;
        Integer size = 10;
        int offset = 0;

        Account secondAccount =
                Account.builder().id(UUID.randomUUID()).accountNumber(1002L).build();

        AccountEntity secondAccountEntity =
                new AccountEntity(
                        secondAccount.getId(),
                        testClientId,
                        1002L,
                        AccountType.CORRIENTE,
                        BigDecimal.valueOf(2000.00),
                        BigDecimal.valueOf(2500.00),
                        true,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 15, 15, 30));

        Flux<AccountEntity> accountEntities = Flux.just(testAccountEntity, secondAccountEntity);

        doReturn(accountEntities)
                .when(accountRepository)
                .findWithFilters(testClientId, size, offset);
        doReturn(testAccount).when(accountMapper).toDomain(testAccountEntity);
        doReturn(secondAccount).when(accountMapper).toDomain(secondAccountEntity);

        // Act
        Flux<Account> result = accountPersistenceAdapter.findAll(testClientId, page, size);

        // Assert
        StepVerifier.create(result)
                .assertNext(account -> assertEquals(testAccount.getId(), account.getId()))
                .assertNext(account -> assertEquals(secondAccount.getId(), account.getId()))
                .verifyComplete();

        verify(accountRepository).findWithFilters(testClientId, size, offset);
        verify(accountMapper).toDomain(testAccountEntity);
        verify(accountMapper).toDomain(secondAccountEntity);
    }

    @Test
    void givenValidAccountWhenUpdateThenReturnUpdatedAccountMono() {
        // Arrange
        Account accountToUpdate =
                Account.builder().currentBalance(BigDecimal.valueOf(2000.00)).build();

        Account updatedAccount = Account.builder().updatedAt(LocalDateTime.now()).build();

        doReturn(Mono.just(updatedAccount))
                .when(transactionService)
                .updateAccount(any(Account.class));

        // Act
        Mono<Account> result = accountPersistenceAdapter.update(accountToUpdate);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        account -> {
                            assertAll(
                                    () -> assertNotNull(account),
                                    () ->
                                            assertEquals(
                                                    updatedAccount.getCurrentBalance(),
                                                    account.getCurrentBalance()),
                                    () -> assertNotNull(account.getUpdatedAt()));
                        })
                .verifyComplete();

        verify(transactionService).updateAccount(any(Account.class));
    }

    @Test
    void givenValidAccountIdAndBalanceWhenUpdateBalanceThenReturnUpdatedAccountMono() {
        // Arrange
        BigDecimal newBalance = BigDecimal.valueOf(3000.00);

        Account foundAccount = Account.builder().build();
        Account updatedAccount = Account.builder().currentBalance(newBalance).build();

        doReturn(Mono.just(testAccountEntity)).when(accountRepository).findById(testAccountId);
        doReturn(foundAccount).when(accountMapper).toDomain(testAccountEntity);
        doReturn(Mono.just(updatedAccount))
                .when(transactionService)
                .updateAccount(any(Account.class));

        // Act
        Mono<Account> result = accountPersistenceAdapter.updateBalance(testAccountId, newBalance);

        // Assert
        StepVerifier.create(result)
                .assertNext(
                        account -> {
                            assertAll(
                                    () -> assertNotNull(account),
                                    () -> assertEquals(newBalance, account.getCurrentBalance()));
                        })
                .verifyComplete();

        verify(accountRepository).findById(testAccountId);
        verify(accountMapper).toDomain(testAccountEntity);
        verify(transactionService).updateAccount(any(Account.class));
    }

    @Test
    void givenValidAccountNumberWhenDeleteByAccountNumberThenReturnMonoVoid() {
        // Arrange
        String accountNumber = "1001";
        Long accountNumberLong = 1001L;

        doReturn(Mono.empty()).when(accountRepository).deleteByAccountNumber(accountNumberLong);

        // Act
        Mono<Void> result = accountPersistenceAdapter.deleteByAccountNumber(accountNumber);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(accountRepository).deleteByAccountNumber(accountNumberLong);
    }
}
