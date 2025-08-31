package com.pichincha.dm.bank.accounts.application.command;

import reactor.core.publisher.Mono;

public interface MovementCommand {

    Mono<Void> execute();
}
