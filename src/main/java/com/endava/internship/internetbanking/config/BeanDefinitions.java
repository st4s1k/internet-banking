package com.endava.internship.internetbanking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
public class BeanDefinitions {

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("");
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public EntityManager getEntityManager(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }
}
