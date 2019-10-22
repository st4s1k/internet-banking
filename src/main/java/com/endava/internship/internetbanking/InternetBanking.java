package com.endava.internship.internetbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InternetBanking {
    public static void main(String[] args) {
        SpringApplication.run(InternetBanking.class, args);
    }
}
