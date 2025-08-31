package com.pichincha.dm.bank.accounts.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories(
        basePackages = "com.pichincha.dm.bank.accounts.infrastructure.adapter.persistence")
public class R2dbcConfig {}
