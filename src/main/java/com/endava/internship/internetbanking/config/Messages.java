package com.endava.internship.internetbanking.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties("messages")
public class Messages {

    public Http http;
    public Exceptions exceptions;

    @Setter
    public static class Http {

        public User user;
        public Account account;
        public Transfer transfer;

        @Setter
        public static class User {

            public Creation creation;

            @Setter
            public static class Creation {
                public String success;
                public String fail;
                public String failExistingUsername;
            }
        }

        @Setter
        public static class Account {

            public Creation creation;

            @Setter
            public static class Creation {
                public String success;
                public String fail;
                public String userNotFound;
            }
        }

        @Setter
        public static class Transfer {
            public String success;
            public String currentAccountNotFound;
            public String targetAccountNotFound;
            public String currentAccountNull;
            public String targetAccountNull;
            public String invalidTransferAmount;
            public String insufficientFunds;
            public String transferObjectNull;
            public String transferAmountNull;
            public String fail;
        }
    }

    @Setter
    public static class Exceptions {

        public Transfer transfer;

        @Setter
        public static class Transfer {
            public String invalidTransferAmount;
            public String insufficientFunds;
            public String transferObjectNull;
            public String transferAmountNull;
            public String badSourceId;
            public String badDestinationId;
            public String loggingFail;
            public String fail;
        }
    }
}
