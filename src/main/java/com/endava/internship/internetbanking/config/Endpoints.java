package com.endava.internship.internetbanking.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties("internetbanking.endpoints")
public class Endpoints {

    public Users users;
    public Accounts accounts;
    public Banking banking;

    @Setter
    public static class Users {
        public String url;
    }

    @Setter
    public static class Accounts {
        public String url;
    }

    @Setter
    public static class Banking {
        public String url;
        public String topUp;
        public String drawDown;
    }
}
