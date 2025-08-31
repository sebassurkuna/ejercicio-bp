package com.pichincha.dm.bank.accounts.application.service;

import com.pichincha.dm.bank.accounts.application.port.input.AccountInputPort;
import com.pichincha.dm.bank.accounts.application.port.input.ClientInputPort;
import com.pichincha.dm.bank.accounts.application.port.input.MovementInputPort;
import com.pichincha.dm.bank.accounts.application.port.input.ReportInputPort;
import com.pichincha.dm.bank.accounts.domain.Account;
import com.pichincha.dm.bank.accounts.domain.Movement;
import com.pichincha.dm.bank.accounts.domain.Person;
import com.pichincha.dm.bank.accounts.domain.Report;
import com.pichincha.dm.bank.accounts.domain.exception.GeneralApplicationException;
import com.pichincha.dm.bank.accounts.domain.service.ReportGeneratorStrategyFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReportApplicationService implements ReportInputPort {

    private final ClientInputPort clientInputPort;
    private final AccountInputPort accountInputPort;
    private final MovementInputPort movementInputPort;
    private final ReportGeneratorStrategyFactory strategyFactory;

    @Override
    public Mono<Report> generateReport(UUID clientId, LocalDate startDate, LocalDate endDate) {
        return validateDateRange(startDate, endDate)
                .then(clientInputPort.getClientById(clientId))
                .switchIfEmpty(
                        Mono.error(
                                new GeneralApplicationException(
                                        "Cliente no encontrado con ID: " + clientId,
                                        "Cliente no encontrado",
                                        HttpStatus.NOT_FOUND)))
                .flatMap(client -> buildReport(client, clientId, startDate, endDate));
    }

    @Override
    public Mono<String> generateFormattedReport(
            UUID clientId, LocalDate startDate, LocalDate endDate, String format) {
        return generateReport(clientId, startDate, endDate)
                .flatMap(report -> strategyFactory.generateReport(report, format));
    }

    private Mono<Void> validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return Mono.error(
                    new IllegalArgumentException(
                            "La fecha de inicio no puede ser posterior a la fecha fin"));
        }
        return Mono.empty();
    }

    private Mono<Report> buildReport(
            Object clientObj, UUID clientId, LocalDate startDate, LocalDate endDate) {
        String clientName = extractClientName(clientObj);

        return accountInputPort
                .listAccounts(clientId, null, null)
                .flatMap(account -> buildAccountSummary(account, startDate, endDate))
                .collectList()
                .map(
                        accountSummaries ->
                                createReport(
                                        clientId,
                                        clientName,
                                        startDate,
                                        endDate,
                                        accountSummaries));
    }

    private String extractClientName(Object clientObj) {
        if (clientObj instanceof com.pichincha.dm.bank.accounts.domain.Client client) {
            Person person = client.getPerson();
            if (person != null) {
                String firstName = person.getName() != null ? person.getName() : "";
                String lastName = person.getLastName() != null ? person.getLastName() : "";
                return (firstName + " " + lastName).trim();
            }
        }
        return "Cliente Desconocido";
    }

    private Report createReport(
            UUID clientId,
            String clientName,
            LocalDate startDate,
            LocalDate endDate,
            List<Report.AccountSummary> accountSummaries) {
        BigDecimal totalDebits =
                accountSummaries.stream()
                        .map(Report.AccountSummary::getTotalDebits)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits =
                accountSummaries.stream()
                        .map(Report.AccountSummary::getTotalCredits)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Report.builder()
                .clientId(clientId)
                .clientName(clientName)
                .startDate(startDate)
                .endDate(endDate)
                .accounts(accountSummaries)
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .build();
    }

    private Mono<Report.AccountSummary> buildAccountSummary(
            Account account, LocalDate startDate, LocalDate endDate) {
        return movementInputPort
                .listMovements(null, account.getAccountNumber(), startDate, endDate, null, null)
                .collectList()
                .map(movements -> buildAccountSummaryFromMovements(account, movements));
    }

    private Report.AccountSummary buildAccountSummaryFromMovements(
            Account account, List<Movement> movements) {
        BigDecimal totalDebits =
                movements.stream()
                        .filter(m -> "DEBITO".equals(m.getType().toString()))
                        .map(Movement::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits =
                movements.stream()
                        .filter(m -> "CREDITO".equals(m.getType().toString()))
                        .map(Movement::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Report.MovementSummary> movementSummaries =
                movements.stream()
                        .map(
                                m ->
                                        Report.MovementSummary.builder()
                                                .id(m.getId())
                                                .date(m.getDate().toLocalDate())
                                                .type(m.getType().toString())
                                                .amount(m.getValue())
                                                .balanceAfterTransaction(m.getPostMovementBalance())
                                                .build())
                        .toList();

        return Report.AccountSummary.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getType().toString())
                .initialBalance(account.getInitBalance())
                .currentBalance(account.getCurrentBalance())
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .movements(movementSummaries)
                .build();
    }
}
