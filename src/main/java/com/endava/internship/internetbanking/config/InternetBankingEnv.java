package com.endava.internship.internetbanking.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties("internetbanking.env")
public class InternetBankingEnv {



    @Setter
    public static class Reconciliation {

        public String rate;

        @Setter
        public static class Creation {
            public String success;
            public String fail;
            public String userNotFound;
        }
    }
}
