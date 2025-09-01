# Estándares de Pruebas Unitarias

Este documento establece los estándares y mejores prácticas para la creación de pruebas unitarias en el proyecto.

## 📁 Estructura de Carpetas

Las pruebas unitarias deben seguir la misma estructura de paquetes que las clases que están probando, pero ubicadas en `src/test/java`:

```
src/
├── main/java/com/pichincha/dm/bank/accounts/
│   ├── application/
│   │   └── service/
│   │       └── AccountService.java
│   ├── domain/
│   │   └── Account.java
│   └── infrastructure/
│       └── adapter/
│           └── AccountRepository.java
└── test/java/com/pichincha/dm/bank/accounts/
    ├── application/
    │   └── service/
    │       └── AccountServiceTest.java
    ├── domain/
    │   └── AccountTest.java
    └── infrastructure/
        └── adapter/
            └── AccountRepositoryTest.java
```

## 🧪 Frameworks y Herramientas

### JUnit 5
- Utilizar JUnit 5 como framework principal de pruebas
- Usar las anotaciones más recientes: `@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`

### Mockito
- Usar Mockito para la creación de mocks y stubs
- Preferir anotaciones sobre creación manual de mocks

## 📋 Anotaciones Recomendadas

```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private AccountService accountService;
    
    @Test
    void shouldReturnAccountWhenValidIdProvided() {
        // Test implementation
    }
}
```

### Anotaciones Principales:
- `@ExtendWith(MockitoExtension.class)` - Para integrar Mockito con JUnit 5
- `@Mock` - Para crear mocks de dependencias
- `@InjectMocks` - Para inyectar mocks en la clase bajo prueba
- `@Spy` - Para crear espías cuando se necesite comportamiento parcial
- `@Captor` - Para capturar argumentos en verificaciones

## 📥 Imports Estáticos

Preferir imports estáticos para mejorar la legibilidad:

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
```

**❌ Evitar:**
```java
Assertions.assertEquals(expected, actual);
Mockito.when(mock.method()).thenReturn(value);
```

**✅ Preferir:**
```java
assertEquals(expected, actual);
when(mock.method()).thenReturn(value);
```

## 🎯 Stubbing con Mockito

Preferir `doReturn().when()` sobre `when().thenReturn()` especialmente para métodos void o cuando hay riesgo de efectos secundarios:

```java
// ✅ Recomendado
doReturn(expectedAccount)
    .when(accountRepository)
    .findById(anyLong());

// ❌ Evitar en casos complejos
when(accountRepository.findById(anyLong())).thenReturn(expectedAccount);
```

### Cuándo usar cada uno:
- `doReturn().when()` - Para métodos void, métodos finales, o cuando se quiere evitar la ejecución del método real
- `when().thenReturn()` - Para casos simples donde no hay riesgo de efectos secundarios

## ✅ Assertions de JUnit

Usar exclusivamente las assertions de JUnit 5:

```java
// Assertions básicas
assertEquals(expected, actual);
assertNotNull(result);
assertTrue(condition);
assertFalse(condition);


// Assertions para excepciones
assertThrows(AccountNotFoundException.class, () -> {
    accountService.findById(invalidId);
});

// Assertions múltiples
assertAll(
    () -> assertEquals(expectedId, account.getId()),
    () -> assertEquals(expectedBalance, account.getBalance()),
    () -> assertNotNull(account.getCreatedDate())
);
```

## 🏷️ Convenciones de Nomenclatura

### Patrón Given-When-Then
```java
@Test
void givenValidAccountIdWhenFindByIdThenReturnAccount() {
    // Given
    Long accountId = 1L;
    Account expectedAccount = Account.builder()
        .id(accountId)
        .balance(BigDecimal.valueOf(1000))
        .build();
    
    doReturn(Optional.of(expectedAccount))
        .when(accountRepository)
        .findById(accountId);
    
    // When
    Account result = accountService.findById(accountId);
    
    // Then
    assertNotNull(result);
    assertEquals(expectedAccount.getId(), result.getId());
    assertEquals(expectedAccount.getBalance(), result.getBalance());
}
```

### Patrón Should-When
```java
@Test
void shouldReturnAccountWhenValidIdProvided() {
    // Arrange
    Long accountId = 1L;
    Account expectedAccount = createTestAccount(accountId);
    
    doReturn(Optional.of(expectedAccount))
        .when(accountRepository)
        .findById(accountId);
    
    // Act
    Account result = accountService.findById(accountId);
    
    // Assert
    assertEquals(expectedAccount, result);
}

@Test
void shouldThrowExceptionWhenAccountNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    
    doReturn(Optional.empty())
        .when(accountRepository)
        .findById(nonExistentId);
    
    // Act & Assert
    assertThrows(AccountNotFoundException.class, () -> {
        accountService.findById(nonExistentId);
    });
}
```

## 🔄 Patrón AAA (Arrange-Act-Assert)

Estructurar las pruebas con comentarios claros que identifiquen cada sección:

```java
@Test
void shouldCreateAccountWhenValidDataProvided() {
    // Arrange
    CreateAccountRequest request = CreateAccountRequest.builder()
        .clientId(1L)
        .initialBalance(BigDecimal.valueOf(1000))
        .accountType(AccountType.SAVINGS)
        .build();
    
    Account expectedAccount = Account.builder()
        .id(1L)
        .clientId(request.getClientId())
        .balance(request.getInitialBalance())
        .type(request.getAccountType())
        .build();
    
    doReturn(expectedAccount)
        .when(accountRepository)
        .save(any(Account.class));
    
    // Act
    Account result = accountService.createAccount(request);
    
    // Assert
    assertNotNull(result);
    assertEquals(expectedAccount.getId(), result.getId());
    assertEquals(expectedAccount.getClientId(), result.getClientId());
    assertEquals(expectedAccount.getBalance(), result.getBalance());
    verify(accountRepository).save(any(Account.class));
    verify(eventPublisher).publishEvent(any(AccountCreatedEvent.class));
}
```

## ⚡ Pruebas para Métodos Reactivos

Para probar métodos que retornan `Mono` o `Flux`, usar `StepVerifier`:

```java
@Test
void shouldReturnAccountMonoWhenValidIdProvided() {
    // Arrange
    Long accountId = 1L;
    Account expectedAccount = createTestAccount(accountId);
    
    doReturn(Mono.just(expectedAccount))
        .when(reactiveAccountRepository)
        .findById(accountId);
    
    // Act
    Mono<Account> result = reactiveAccountService.findById(accountId);
    
    // Assert
    StepVerifier.create(result)
        .expectNext(expectedAccount)
        .verifyComplete();
}

@Test
void shouldReturnErrorWhenAccountNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    
    doReturn(Mono.empty())
        .when(reactiveAccountRepository)
        .findById(nonExistentId);
    
    // Act
    Mono<Account> result = reactiveAccountService.findById(nonExistentId);
    
    // Assert
    StepVerifier.create(result)
        .expectError(AccountNotFoundException.class)
        .verify();
}
```

## 🧬 Pruebas de Mutación

Para asegurar que las pruebas puedan resistir pruebas de mutación:

### ✅ Buenas Prácticas:
1. **Verificar valores específicos, no solo existencia:**
```java
// ✅ Correcto
assertEquals(BigDecimal.valueOf(1500), account.getBalance());

// ❌ Insuficiente para pruebas de mutación
assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
```

2. **Verificar todos los campos relevantes:**
```java
@Test
void shouldUpdateAccountBalanceWhenValidTransaction() {
    // Arrange
    Account account = createTestAccount(1L, BigDecimal.valueOf(1000));
    BigDecimal transactionAmount = BigDecimal.valueOf(500);
    
    // Act
    Account result = accountService.processDebit(account, transactionAmount);
    
    // Assert
    assertEquals(BigDecimal.valueOf(500), result.getBalance()); // Valor específico
    assertEquals(account.getId(), result.getId()); // ID no cambió
    assertNotNull(result.getLastModified()); // Timestamp actualizado
}
```

3. **Probar condiciones límite:**
```java
@Test
void shouldThrowExceptionWhenBalanceInsufficientForDebit() {
    // Arrange
    Account account = createTestAccount(1L, BigDecimal.valueOf(100));
    BigDecimal debitAmount = BigDecimal.valueOf(200);
    
    // Act & Assert
    InsufficientFundsException exception = assertThrows(
        InsufficientFundsException.class,
        () -> accountService.processDebit(account, debitAmount)
    );
    
    assertEquals("Insufficient funds for debit operation", exception.getMessage());
}
```

## 📊 Cobertura de Código

### Principios:
- **Objetivo:** 100% de cobertura de líneas y ramas
- **Calidad sobre Cantidad:** Preferir pocos casos de prueba bien diseñados que cubran todos los escenarios
- **No sobre-testear:** Crear solo los casos necesarios para alcanzar la cobertura completa

### Casos de Prueba Esenciales:
1. **Happy Path** - Flujo principal exitoso
2. **Error Cases** - Manejo de excepciones y errores
3. **Edge Cases** - Casos límite y valores especiales
4. **Boundary Conditions** - Valores en los límites de validación

### Ejemplo de Cobertura Completa:
```java
@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {
    
    @InjectMocks
    private AccountValidator accountValidator;
    
    @Test
    void shouldReturnTrueWhenAccountIsValid() {
        // Happy path
        Account validAccount = createValidAccount();
        
        boolean result = accountValidator.isValid(validAccount);
        
        assertTrue(result);
    }
    
    @Test
    void shouldReturnFalseWhenAccountIsNull() {
        // Null case
        boolean result = accountValidator.isValid(null);
        
        assertFalse(result);
    }
    
    @Test
    void shouldReturnFalseWhenBalanceIsNegative() {
        // Boundary condition
        Account accountWithNegativeBalance = Account.builder()
            .balance(BigDecimal.valueOf(-1))
            .build();
        
        boolean result = accountValidator.isValid(accountWithNegativeBalance);
        
        assertFalse(result);
    }
}
```

## 🛠️ Herramientas de Utilidad

### Builders para Datos de Prueba:
```java
public class TestDataBuilder {
    
    public static Account createTestAccount(Long id, BigDecimal balance) {
        return Account.builder()
            .id(id)
            .clientId(1L)
            .balance(balance)
            .type(AccountType.SAVINGS)
            .status(AccountStatus.ACTIVE)
            .createdDate(LocalDateTime.now())
            .build();
    }
    
    public static Account createValidAccount() {
        return createTestAccount(1L, BigDecimal.valueOf(1000));
    }
}
```

### Configuración Base para Pruebas:
```java
@ExtendWith(MockitoExtension.class)
abstract class BaseUnitTest {
    
    @BeforeEach
    void setUp() {
        // Configuración común para todas las pruebas
    }
    
    @AfterEach
    void tearDown() {
        // Limpieza después de cada prueba
    }
}
```

## 📝 Checklist de Revisión

Antes de considerar completa una prueba unitaria, verificar:

- [ ] ✅ Estructura de carpetas coherente con la clase bajo prueba
- [ ] ✅ Uso de JUnit 5 y Mockito con anotaciones
- [ ] ✅ Imports estáticos aplicados donde sea posible
- [ ] ✅ Uso de `doReturn().when()` para stubbing
- [ ] ✅ Solo assertions de JUnit utilizadas
- [ ] ✅ Nomenclatura sigue patrón `given-when-then` o `should-when`
- [ ] ✅ Patrón AAA con comentarios claros
- [ ] ✅ StepVerifier usado para métodos reactivos
- [ ] ✅ Pruebas resistentes a mutaciones (valores específicos)
- [ ] ✅ Cobertura 100% con casos mínimos necesarios
- [ ] ✅ Verificaciones de mocks cuando sea relevante
- [ ] ✅ Manejo adecuado de excepciones y casos edge

---

**Nota:** Estos estándares deben aplicarse consistentemente en todo el proyecto para mantener la calidad y uniformidad del código de pruebas.
