package org.example.config;

public class DatabaseConfig {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DATABASE_NAME = "quanlynhansu";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "123456";

    private final String host;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;

    public DatabaseConfig(String host, int port, String databaseName, String username, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public static DatabaseConfig fromEnvironment() {
        return new DatabaseConfig(
                getValue("DB_HOST", "db.host", DEFAULT_HOST),
                getIntValue("DB_PORT", "db.port", DEFAULT_PORT),
                getValue("DB_NAME", "db.name", DEFAULT_DATABASE_NAME),
                getValue("DB_USER", "db.user", DEFAULT_USERNAME),
                getValue("DB_PASSWORD", "db.password", DEFAULT_PASSWORD)
        );
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static String getValue(String environmentKey, String propertyKey, String defaultValue) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return defaultValue;
    }

    private static int getIntValue(String environmentKey, String propertyKey, int defaultValue) {
        String value = getValue(environmentKey, propertyKey, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
