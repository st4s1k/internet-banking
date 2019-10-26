package com.endava.internship.internetbanking.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties("internetbanking.env")
public class InternetBankingEnv {

    public Reconciliation reconciliation;

    @Setter
    public static class Reconciliation {
        public Long rate;
    }
}
