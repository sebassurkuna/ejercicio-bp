package com.pichincha.dm.bank.accounts.application.service;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.pichincha.dm.bank.accounts.application.port.output.AccountOutputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.enums.AccountType;
import com.pichincha.dm.bank.accounts.util.AccountNumberGenerator;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {

    @Mock private AccountOutputPort accountOutputPort;

    @InjectMocks private AccountApplicationService accountApplicationService;

    private Account testAccount;
    private UUID clientId;
    private String accountNumber;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        accountNumber = "12345678901";

        testAccount =
                Account.builder()
                        .id(UUID.randomUUID())
                        .clientId(clientId)
                        .accountNumber(Long.valueOf(accountNumber))
                        .type(AccountType.AHORROS)
                        .initBalance(valueOf(1000))
                        .currentBalance(valueOf(1000))
                        .state(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void givenValidAccountWhenCreateAccountThenSetFieldsAndSave() {
        // Arrange
        Account inputAccount =
                Account.builder()
                        .clientId(clientId)
                        .type(AccountType.CORRIENTE)
                        .initBalance(valueOf(500))
                        .build();

        Account expectedSavedAccount =
                Account.builder()
                        .id(UUID.randomUUID())
                        .clientId(clientId)
                        .accountNumber(24081234567L)
                        .type(AccountType.CORRIENTE)
                        .initBalance(valueOf(500))
                        .currentBalance(valueOf(500))
                        .state(true)
                        .createdAt(LocalDateTime.now())
                        .build();

        doReturn(Mono.just(expectedSavedAccount)).when(accountOutputPort).save(any(Account.class));

        try (MockedStatic<AccountNumberGenerator> mockGenerator =
                Mockito.mockStatic(AccountNumberGenerator.class)) {

            mockGenerator
                    .when(AccountNumberGenerator::generateAccountNumberAsLong)
                    .thenReturn(24081234567L);

            // Act
            Mono<Account> result = accountApplicationService.createAccount(inputAccount);

            // Assert
            StepVerifier.create(result).expectNext(expectedSavedAccount).verifyComplete();

            assertTrue(inputAccount.getState());
            assertNotNull(inputAccount.getAccountNumber());
            assertEquals(inputAccount.getInitBalance(), inputAccount.getCurrentBalance());

            verify(accountOutputPort).save(inputAccount);
        }
    }

    @Test
    void givenValidAccountNumberWhenGetAccountByNumberThenReturnAccount() {
        // Arrange
        doReturn(Mono.just(testAccount)).when(accountOutputPort).findByAccountNumber(accountNumber);

        // Act
        Mono<Account> result = accountApplicationService.getAccountByNumber(accountNumber);

        // Assert
        StepVerifier.create(result).expectNext(testAccount).verifyComplete();

        verify(accountOutputPort).findByAccountNumber(accountNumber);
    }

    @Test
    void givenValidParametersWhenListAccountsThenReturnAccountFlux() {
        // Arrange
        Account account1 = createTestAccountWithId(UUID.randomUUID());
        Account account2 = createTestAccountWithId(UUID.randomUUID());
        Flux<Account> expectedAccounts = Flux.just(account1, account2);

        Integer page = 0;
        Integer size = 10;

        doReturn(expectedAccounts).when(accountOutputPort).findAll(clientId, page, size);

        // Act
        Flux<Account> result = accountApplicationService.listAccounts(clientId, page, size);

        // Assert
        StepVerifier.create(result).expectNext(account1).expectNext(account2).verifyComplete();

        verify(accountOutputPort).findAll(clientId, page, size);
    }

    @Test
    void givenValidAccountWhenUpdateAccountThenReturnUpdatedAccount() {
        // Arrange
        Account updatedAccount =
                Account.builder()
                        .type(AccountType.CORRIENTE)
                        .state(false)
                        .updatedAt(LocalDateTime.now())
                        .build();

        doReturn(Mono.just(updatedAccount)).when(accountOutputPort).update(testAccount);

        // Act
        Mono<Account> result = accountApplicationService.updateAccount(testAccount);

        // Assert
        StepVerifier.create(result).expectNext(updatedAccount).verifyComplete();

        verify(accountOutputPort).update(testAccount);
    }

    @Test
    void givenValidAccountNumberWhenDeleteAccountThenCompleteSuccessfully() {
        // Arrange
        doReturn(Mono.empty()).when(accountOutputPort).deleteByAccountNumber(accountNumber);

        // Act
        Mono<Void> result = accountApplicationService.deleteAccount(accountNumber);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(accountOutputPort).deleteByAccountNumber(accountNumber);
    }

    private Account createTestAccountWithId(UUID id) {
        return Account.builder()
                .id(id)
                .clientId(clientId)
                .accountNumber(
                        Long.valueOf(
                                "1234567890" + id.toString().replaceAll("\\D", "").substring(0, 1)))
                .type(AccountType.CORRIENTE)
                .initBalance(valueOf(500))
                .currentBalance(valueOf(500))
                .state(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
