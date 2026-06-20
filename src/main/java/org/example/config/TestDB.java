package org.example.config;

import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        AppContext appContext = AppContext.getInstance();
        DatabaseConfig databaseConfig = appContext.getDatabaseConfig();
        DatabaseConnection databaseConnection = appContext.getDatabaseConnection();

        System.out.println("Dang test ket noi MySQL...");
        System.out.println("URL: " + databaseConfig.getJdbcUrl());
        System.out.println("User: " + databaseConfig.getUsername());

        try {
            Connection connection = databaseConnection.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Ket noi database THANH CONG!");
            } else {
                System.out.println("Ket noi database THAT BAI!");
            }
        } catch (SQLException exception) {
            System.out.println("Ket noi database THAT BAI!");
            System.out.println("Ly do: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            databaseConnection.closeConnection();
        }
    }
}