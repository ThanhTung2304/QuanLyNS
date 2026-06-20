package org.example.config;

import org.example.database.DatabaseConnection;

public class AppContext {
    private static AppContext instance;

    private final DatabaseConfig databaseConfig;
    private final DatabaseConnection databaseConnection;

    private AppContext() {
        this.databaseConfig = DatabaseConfig.fromEnvironment();
        this.databaseConnection = new DatabaseConnection(databaseConfig);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }

        return instance;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}
