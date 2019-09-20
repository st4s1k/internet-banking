package com.endava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OracleDBConnection {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection =
                         DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCLPDB1", "stanislav", "sgirlea12")) {

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from user");

                // do something with result set
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
