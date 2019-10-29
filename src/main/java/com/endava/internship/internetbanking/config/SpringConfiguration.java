package com.endava.internship.internetbanking.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;

@Slf4j
@Configuration
public class SpringConfiguration {

    // TODO: GRACEFUL SHUTDOWN...
    //  https://github.com/eugenp/tutorials/tree/master/spring-boot-deployment/src/main/java/com/baeldung/gracefulshutdown44

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(30);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutdown initiated");
    }
}
