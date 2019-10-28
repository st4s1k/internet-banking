package com.endava.internship.internetbanking.config;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class BeanDefinition {

    @Bean
    public Gson prettyPrintJSON() {
        return new Gson().newBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (date, typeOfSrc, context) -> new JsonPrimitive(date.toString()))
                .create();
    }
}
