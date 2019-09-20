package com.endava.config;

import com.endava.repositories.AccountRepository;
import com.endava.repositories.UserRepository;
import com.endava.sevices.AccountService;
import com.endava.sevices.BankingService;
import com.endava.sevices.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public BankingService getBankingService() {
        return new BankingService();
    }

    @Bean
    public UserService getUserService() {
        return new UserService();
    }

    @Bean
    public AccountService getAccountService() {
        return new AccountService();
    }

    @Bean
    public UserRepository getUserRepo() {
        return new UserRepository();
    }

    @Bean
    public AccountRepository getAccountRepo() {
        return new AccountRepository();
    }
}
