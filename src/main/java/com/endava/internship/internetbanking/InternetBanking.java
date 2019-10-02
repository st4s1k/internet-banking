package com.endava.internship.internetbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:message.properties")
public class InternetBanking {
    public static void main(String[] args) {
        SpringApplication.run(InternetBanking.class, args);
    }
}
