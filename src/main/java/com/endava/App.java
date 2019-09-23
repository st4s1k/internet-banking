package com.endava;

import com.endava.config.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main( String[] args )    {
        SpringApplication.run(App.class, args);
        DatabaseConnection connection = new DatabaseConnection();
        connection.initDB();
    }
}
